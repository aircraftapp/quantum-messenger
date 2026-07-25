package com.example.network.p2p

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class P2pWebSocketServer(
    val port: Int = 8888,
    private val onEnvelopeReceived: (P2pEnvelope, String) -> Unit, // envelope, clientRemoteAddress
    private val onPeerConnected: (remoteAddress: String) -> Unit = {},
    private val onPeerDisconnected: (remoteAddress: String) -> Unit = {}
) {
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeClients = ConcurrentHashMap<String, SocketClientSession>()

    private class SocketClientSession(
        val socket: Socket,
        val inputStream: InputStream,
        val outputStream: OutputStream,
        val remoteAddress: String
    )

    fun start() {
        if (isRunning) return
        isRunning = true
        serverScope.launch {
            try {
                serverSocket = ServerSocket(port)
                Log.i("P2pWebSocketServer", "P2P WebSocket Server started on port $port")
                while (isRunning && !serverSocket!!.isClosed) {
                    val clientSocket = serverSocket!!.accept()
                    val remoteAddr = "${clientSocket.inetAddress.hostAddress}:${clientSocket.port}"
                    launch { handleClientSocket(clientSocket, remoteAddr) }
                }
            } catch (e: Exception) {
                if (isRunning) {
                    Log.e("P2pWebSocketServer", "Server socket error: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            activeClients.values.forEach { session ->
                runCatching { session.socket.close() }
            }
            activeClients.clear()
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e("P2pWebSocketServer", "Error stopping server: ${e.message}")
        }
        serverScope.cancel()
    }

    fun broadcastMessage(jsonMessage: String) {
        activeClients.values.forEach { session ->
            sendWebSocketFrame(session.outputStream, jsonMessage)
        }
    }

    fun sendToAddress(remoteAddress: String, jsonMessage: String): Boolean {
        val session = activeClients[remoteAddress] ?: return false
        return sendWebSocketFrame(session.outputStream, jsonMessage)
    }

    private fun handleClientSocket(socket: Socket, remoteAddress: String) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            // Step 1: Read HTTP Handshake Header
            val headerBuffer = ByteArray(2048)
            val bytesRead = input.read(headerBuffer)
            if (bytesRead <= 0) {
                socket.close()
                return
            }

            val requestStr = String(headerBuffer, 0, bytesRead)
            if (!requestStr.contains("Upgrade: websocket", ignoreCase = true) &&
                !requestStr.contains("Upgrade: WebSocket", ignoreCase = true)) {
                // Not a websocket handshake
                socket.close()
                return
            }

            // Extract Sec-WebSocket-Key
            val keyRegex = "Sec-WebSocket-Key:\\s*([^\\r\\n]+)".toRegex(RegexOption.IGNORE_CASE)
            val match = keyRegex.find(requestStr)
            val clientKey = match?.groupValues?.get(1)?.trim() ?: ""

            // Calculate Sec-WebSocket-Accept
            val acceptKey = calculateWebSocketAccept(clientKey)

            val handshakeResponse = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: $acceptKey\r\n\r\n"

            output.write(handshakeResponse.toByteArray(Charsets.UTF_8))
            output.flush()

            val session = SocketClientSession(socket, input, output, remoteAddress)
            activeClients[remoteAddress] = session
            onPeerConnected(remoteAddress)

            // Step 2: Receive WebSocket frames
            while (isRunning && !socket.isClosed) {
                val frameText = readWebSocketFrame(input) ?: break
                val envelope = P2pEnvelope.fromJson(frameText)
                if (envelope != null) {
                    onEnvelopeReceived(envelope, remoteAddress)
                }
            }
        } catch (e: Exception) {
            Log.d("P2pWebSocketServer", "Client $remoteAddress disconnected: ${e.message}")
        } finally {
            activeClients.remove(remoteAddress)
            runCatching { socket.close() }
            onPeerDisconnected(remoteAddress)
        }
    }

    private fun calculateWebSocketAccept(key: String): String {
        val magicGuid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
        val digest = MessageDigest.getInstance("SHA-1")
        val hash = digest.digest((key + magicGuid).toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    private fun readWebSocketFrame(input: InputStream): String? {
        val b0 = input.read()
        if (b0 == -1) return null
        val b1 = input.read()
        if (b1 == -1) return null

        val isMasked = (b1 and 0x80) != 0
        var payloadLength = (b1 and 0x7F).toLong()

        if (payloadLength == 126L) {
            val l0 = input.read()
            val l1 = input.read()
            if (l0 == -1 || l1 == -1) return null
            payloadLength = ((l0 and 0xFF) shl 8 or (l1 and 0xFF)).toLong()
        } else if (payloadLength == 127L) {
            var len: Long = 0
            for (i in 0 until 8) {
                val b = input.read()
                if (b == -1) return null
                len = (len shl 8) or (b and 0xFF).toLong()
            }
            payloadLength = len
        }

        val maskingKey = ByteArray(4)
        if (isMasked) {
            val bytesRead = input.read(maskingKey, 0, 4)
            if (bytesRead < 4) return null
        }

        val payload = ByteArray(payloadLength.toInt())
        var readTotal = 0
        while (readTotal < payloadLength) {
            val read = input.read(payload, readTotal, payloadLength.toInt() - readTotal)
            if (read == -1) return null
            readTotal += read
        }

        if (isMasked) {
            for (i in payload.indices) {
                payload[i] = (payload[i].toInt() xor maskingKey[i % 4].toInt()).toByte()
            }
        }

        return String(payload, Charsets.UTF_8)
    }

    private fun sendWebSocketFrame(output: OutputStream, text: String): Boolean {
        return try {
            val bytes = text.toByteArray(Charsets.UTF_8)
            val length = bytes.size

            output.write(0x81) // Text frame (FIN = 1, Opcode = 1)

            when {
                length <= 125 -> {
                    output.write(length)
                }
                length <= 65535 -> {
                    output.write(126)
                    output.write((length shr 8) and 0xFF)
                    output.write(length and 0xFF)
                }
                else -> {
                    output.write(127)
                    for (i in 7 downTo 0) {
                        output.write(((length.toLong() shr (i * 8)) and 0xFF).toInt())
                    }
                }
            }

            output.write(bytes)
            output.flush()
            true
        } catch (e: Exception) {
            false
        }
    }
}
