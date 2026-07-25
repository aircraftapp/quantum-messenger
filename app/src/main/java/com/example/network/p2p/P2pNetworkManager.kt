package com.example.network.p2p

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.NetworkInterface
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

enum class P2pServerStatus {
    OFFLINE,
    STARTING,
    LISTENING,
    ERROR
}

class P2pNetworkManager private constructor(
    private val context: Context
) {
    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var serverPort = 8888
    private var webSocketServer: P2pWebSocketServer? = null

    private val _serverStatus = MutableStateFlow(P2pServerStatus.OFFLINE)
    val serverStatus: StateFlow<P2pServerStatus> = _serverStatus.asStateFlow()

    private val _serverInfo = MutableStateFlow("P2P Network Offline")
    val serverInfo: StateFlow<String> = _serverInfo.asStateFlow()

    private val _activePeers = MutableStateFlow<List<P2pPeerNode>>(emptyList())
    val activePeers: StateFlow<List<P2pPeerNode>> = _activePeers.asStateFlow()

    private val outboundClients = ConcurrentHashMap<String, P2pWebSocketClient>() // address -> client
    private val peerAddressMap = ConcurrentHashMap<String, String>() // nodeId -> address
    private val peerDetailsMap = ConcurrentHashMap<String, P2pPeerNode>() // nodeId -> PeerNode

    private val _isBatterySaverMode = MutableStateFlow(false)
    val isBatterySaverMode: StateFlow<Boolean> = _isBatterySaverMode.asStateFlow()

    fun setBatterySaverMode(enabled: Boolean) {
        _isBatterySaverMode.value = enabled
        Log.i("P2pNetworkManager", "Battery Saver Mode set to $enabled. P2P check-in interval: ${if (enabled) 60 else 5} seconds.")
    }

    // Listener for incoming decrypted messages to store in Room DB
    var onIncomingMessageListener: ((senderNodeId: String, senderName: String, chatId: String, encryptedPayload: String, pqcPublicKey: String?) -> Unit)? = null
    var onIncomingWalkieTalkieListener: ((senderNodeId: String, senderName: String, chatId: String, channel: String, durationSeconds: Int, encryptedPayload: String) -> Unit)? = null
    var onNewPeerDiscoveredListener: ((nodeId: String, name: String, address: String, pqcPublicKey: String) -> Unit)? = null

    companion object {
        @Volatile
        private var instance: P2pNetworkManager? = null

        fun getInstance(context: Context): P2pNetworkManager {
            return instance ?: synchronized(this) {
                instance ?: P2pNetworkManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun startP2pServer(
        port: Int = 8888,
        myNodeId: String,
        myName: String,
        myPqcPublicKey: String
    ) {
        if (_serverStatus.value == P2pServerStatus.LISTENING) return
        this.serverPort = port
        _serverStatus.value = P2pServerStatus.STARTING

        try {
            webSocketServer = P2pWebSocketServer(
                port = port,
                onEnvelopeReceived = { envelope, remoteAddress ->
                    handleIncomingEnvelope(envelope, remoteAddress, myNodeId, myName, myPqcPublicKey)
                },
                onPeerConnected = { remoteAddr ->
                    Log.i("P2pNetworkManager", "Incoming peer connection from $remoteAddr")
                },
                onPeerDisconnected = { remoteAddr ->
                    Log.i("P2pNetworkManager", "Incoming peer disconnected $remoteAddr")
                    removePeerByAddress(remoteAddr)
                }
            )

            webSocketServer?.start()

            val localIp = getLocalIpAddress()
            _serverStatus.value = P2pServerStatus.LISTENING
            _serverInfo.value = "Listening on ws://$localIp:$port"
            Log.i("P2pNetworkManager", "P2P WebSocket Server running on $localIp:$port")

        } catch (e: Exception) {
            _serverStatus.value = P2pServerStatus.ERROR
            _serverInfo.value = "Failed to start server: ${e.localizedMessage}"
            Log.e("P2pNetworkManager", "Server error: ${e.message}")
        }
    }

    fun stopP2pServer() {
        webSocketServer?.stop()
        webSocketServer = null

        outboundClients.values.forEach { it.disconnect() }
        outboundClients.clear()
        peerAddressMap.clear()
        peerDetailsMap.clear()

        _activePeers.value = emptyList()
        _serverStatus.value = P2pServerStatus.OFFLINE
        _serverInfo.value = "P2P Network Offline"
    }

    fun connectToPeer(
        peerAddress: String, // e.g. "192.168.1.150:8888" or "10.0.2.2:8888"
        myNodeId: String,
        myName: String,
        myPqcPublicKey: String,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        val formattedAddr = peerAddress.trim().removePrefix("ws://").removePrefix("wss://")
        if (outboundClients.containsKey(formattedAddr) && outboundClients[formattedAddr]?.isConnected == true) {
            onResult(true, "Already connected to $formattedAddr")
            return
        }

        managerScope.launch {
            val client = P2pWebSocketClient(
                peerAddress = formattedAddr,
                onEnvelopeReceived = { envelope ->
                    handleIncomingEnvelope(envelope, formattedAddr, myNodeId, myName, myPqcPublicKey)
                },
                onConnectionStateChanged = { connected, errorMsg ->
                    if (connected) {
                        // Send HANDSHAKE envelope to peer
                        val handshake = P2pEnvelope(
                            type = P2pMessageType.HANDSHAKE,
                            senderNodeId = myNodeId,
                            senderName = myName,
                            pqcPublicKey = myPqcPublicKey
                        )
                        outboundClients[formattedAddr]?.sendEnvelope(handshake)
                        onResult(true, "Connected to $formattedAddr")
                    } else {
                        outboundClients.remove(formattedAddr)
                        removePeerByAddress(formattedAddr)
                        if (errorMsg != null) {
                            onResult(false, errorMsg)
                        }
                    }
                }
            )

            outboundClients[formattedAddr] = client
            client.connect()
        }
    }

    fun sendDirectMessage(
        targetNodeId: String,
        chatId: String,
        messageId: String,
        encryptedContent: String,
        myNodeId: String,
        myName: String
    ): Boolean {
        val envelope = P2pEnvelope(
            type = P2pMessageType.DIRECT_MESSAGE,
            senderNodeId = myNodeId,
            senderName = myName,
            recipientNodeId = targetNodeId,
            chatId = chatId,
            messageId = messageId,
            encryptedContent = encryptedContent,
            timestamp = System.currentTimeMillis()
        )

        val targetAddress = peerAddressMap[targetNodeId]

        // 1. Try sending via active outbound WebSocket client
        if (targetAddress != null && outboundClients.containsKey(targetAddress)) {
            val sent = outboundClients[targetAddress]?.sendEnvelope(envelope) == true
            if (sent) return true
        }

        // 2. Try sending via server connected sockets
        if (webSocketServer != null && targetAddress != null) {
            val sent = webSocketServer!!.sendToAddress(targetAddress, envelope.toJson())
            if (sent) return true
        }

        // 3. Fallback broadcast to all connected direct peers
        if (webSocketServer != null) {
            webSocketServer!!.broadcastMessage(envelope.toJson())
            return true
        }

        return false
    }

    fun sendWalkieTalkiePttMessage(
        targetNodeId: String,
        chatId: String,
        channel: String,
        durationSeconds: Int,
        encryptedContent: String,
        myNodeId: String,
        myName: String
    ): Boolean {
        val envelope = P2pEnvelope(
            type = P2pMessageType.WALKIE_TALKIE_PTT,
            senderNodeId = myNodeId,
            senderName = myName,
            recipientNodeId = targetNodeId,
            chatId = chatId,
            messageId = "PTT-" + java.util.UUID.randomUUID().toString().take(8),
            encryptedContent = encryptedContent,
            pqcPublicKey = "$channel::$durationSeconds",
            timestamp = System.currentTimeMillis()
        )

        val targetAddress = peerAddressMap[targetNodeId]

        if (targetAddress != null && outboundClients.containsKey(targetAddress)) {
            val sent = outboundClients[targetAddress]?.sendEnvelope(envelope) == true
            if (sent) return true
        }

        if (webSocketServer != null && targetAddress != null) {
            val sent = webSocketServer!!.sendToAddress(targetAddress, envelope.toJson())
            if (sent) return true
        }

        if (webSocketServer != null) {
            webSocketServer!!.broadcastMessage(envelope.toJson())
            return true
        }

        return false
    }

    fun pingPeer(targetNodeId: String, myNodeId: String, myName: String) {
        val envelope = P2pEnvelope(
            type = P2pMessageType.PING,
            senderNodeId = myNodeId,
            senderName = myName,
            recipientNodeId = targetNodeId
        )
        val address = peerAddressMap[targetNodeId] ?: return
        outboundClients[address]?.sendEnvelope(envelope)
            ?: webSocketServer?.sendToAddress(address, envelope.toJson())
    }

    private fun handleIncomingEnvelope(
        envelope: P2pEnvelope,
        remoteAddress: String,
        myNodeId: String,
        myName: String,
        myPqcPublicKey: String
    ) {
        Log.d("P2pNetworkManager", "Received ${envelope.type} from ${envelope.senderNodeId} ($remoteAddress)")

        when (envelope.type) {
            P2pMessageType.HANDSHAKE -> {
                val peer = P2pPeerNode(
                    nodeId = envelope.senderNodeId,
                    name = envelope.senderName,
                    address = remoteAddress,
                    pqcPublicKey = envelope.pqcPublicKey ?: ""
                )
                peerAddressMap[envelope.senderNodeId] = remoteAddress
                peerDetailsMap[envelope.senderNodeId] = peer
                updateActivePeersList()

                onNewPeerDiscoveredListener?.invoke(
                    envelope.senderNodeId,
                    envelope.senderName,
                    remoteAddress,
                    envelope.pqcPublicKey ?: ""
                )

                // Reply with HANDSHAKE if needed
                val replyHandshake = P2pEnvelope(
                    type = P2pMessageType.PONG,
                    senderNodeId = myNodeId,
                    senderName = myName,
                    pqcPublicKey = myPqcPublicKey
                )
                webSocketServer?.sendToAddress(remoteAddress, replyHandshake.toJson())
                    ?: outboundClients[remoteAddress]?.sendEnvelope(replyHandshake)
            }

            P2pMessageType.PONG -> {
                val peer = P2pPeerNode(
                    nodeId = envelope.senderNodeId,
                    name = envelope.senderName,
                    address = remoteAddress,
                    pqcPublicKey = envelope.pqcPublicKey ?: ""
                )
                peerAddressMap[envelope.senderNodeId] = remoteAddress
                peerDetailsMap[envelope.senderNodeId] = peer
                updateActivePeersList()
            }

            P2pMessageType.PING -> {
                val pong = P2pEnvelope(
                    type = P2pMessageType.PONG,
                    senderNodeId = myNodeId,
                    senderName = myName,
                    pqcPublicKey = myPqcPublicKey
                )
                webSocketServer?.sendToAddress(remoteAddress, pong.toJson())
                    ?: outboundClients[remoteAddress]?.sendEnvelope(pong)
            }

            P2pMessageType.DIRECT_MESSAGE -> {
                val encrypted = envelope.encryptedContent
                if (!encrypted.isNullOrBlank()) {
                    onIncomingMessageListener?.invoke(
                        envelope.senderNodeId,
                        envelope.senderName,
                        envelope.chatId ?: envelope.senderNodeId,
                        encrypted,
                        envelope.pqcPublicKey
                    )
                }
            }

            P2pMessageType.WALKIE_TALKIE_PTT -> {
                val encrypted = envelope.encryptedContent
                if (!encrypted.isNullOrBlank()) {
                    val metaParts = envelope.pqcPublicKey?.split("::") ?: emptyList()
                    val channel = metaParts.getOrNull(0) ?: "CH-01 (446.006 MHz)"
                    val durSeconds = metaParts.getOrNull(1)?.toIntOrNull() ?: 3

                    onIncomingWalkieTalkieListener?.invoke(
                        envelope.senderNodeId,
                        envelope.senderName,
                        envelope.chatId ?: envelope.senderNodeId,
                        channel,
                        durSeconds,
                        encrypted
                    )

                    onIncomingMessageListener?.invoke(
                        envelope.senderNodeId,
                        envelope.senderName,
                        envelope.chatId ?: envelope.senderNodeId,
                        encrypted,
                        "WALKIE_TALKIE::$channel::$durSeconds"
                    )
                }
            }

            P2pMessageType.DISCONNECT -> {
                removePeerByAddress(remoteAddress)
            }
        }
    }

    private fun removePeerByAddress(address: String) {
        val entry = peerAddressMap.entries.find { it.value == address }
        if (entry != null) {
            peerAddressMap.remove(entry.key)
            peerDetailsMap.remove(entry.key)
            updateActivePeersList()
        }
    }

    private fun updateActivePeersList() {
        _activePeers.value = peerDetailsMap.values.toList()
    }

    fun getLocalIpAddress(): String {
        return try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (isIPv4 && (sAddr.startsWith("192.168.") || sAddr.startsWith("10.") || sAddr.startsWith("172."))) {
                            return sAddr
                        }
                    }
                }
            }
            "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }
}
