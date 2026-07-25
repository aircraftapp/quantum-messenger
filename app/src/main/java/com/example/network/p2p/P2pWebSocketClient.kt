package com.example.network.p2p

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class P2pWebSocketClient(
    private val peerAddress: String, // "192.168.1.100:8888"
    private val onEnvelopeReceived: (P2pEnvelope) -> Unit,
    private val onConnectionStateChanged: (Boolean, String?) -> Unit = { _, _ -> }
) {
    private var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep-alive websocket
        .build()

    private var webSocket: WebSocket? = null
    var isConnected: Boolean = false
        private set

    fun connect() {
        val url = if (peerAddress.startsWith("ws://") || peerAddress.startsWith("wss://")) {
            peerAddress
        } else {
            "ws://$peerAddress"
        }

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                isConnected = true
                Log.i("P2pWebSocketClient", "Connected to peer at $url")
                onConnectionStateChanged(true, null)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                val envelope = P2pEnvelope.fromJson(text)
                if (envelope != null) {
                    onEnvelopeReceived(envelope)
                }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d("P2pWebSocketClient", "Connection closing: $reason")
                onConnectionStateChanged(false, reason)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d("P2pWebSocketClient", "Connection closed: $reason")
                onConnectionStateChanged(false, reason)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e("P2pWebSocketClient", "WebSocket failure to $url: ${t.localizedMessage}")
                onConnectionStateChanged(false, t.localizedMessage ?: "Connection error")
            }
        })
    }

    fun sendEnvelope(envelope: P2pEnvelope): Boolean {
        return if (isConnected && webSocket != null) {
            webSocket!!.send(envelope.toJson())
        } else {
            false
        }
    }

    fun disconnect() {
        isConnected = false
        webSocket?.close(1000, "Disconnected by user")
        webSocket = null
    }
}
