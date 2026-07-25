package com.example.network.p2p

import org.json.JSONObject

enum class P2pMessageType {
    HANDSHAKE,
    DIRECT_MESSAGE,
    WALKIE_TALKIE_PTT,
    PING,
    PONG,
    DISCONNECT
}

data class P2pPeerNode(
    val nodeId: String,
    val name: String,
    val address: String, // e.g. "192.168.1.120:8888"
    val pqcPublicKey: String,
    val isOnline: Boolean = true,
    val lastPingMs: Long = System.currentTimeMillis(),
    val connectionType: String = "WEBSOCKET_DIRECT"
)

data class P2pEnvelope(
    val type: P2pMessageType,
    val senderNodeId: String,
    val senderName: String,
    val recipientNodeId: String? = null,
    val chatId: String? = null,
    val messageId: String? = null,
    val encryptedContent: String? = null,
    val pqcPublicKey: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val signature: String? = null
) {
    fun toJson(): String {
        val json = JSONObject()
        json.put("type", type.name)
        json.put("senderNodeId", senderNodeId)
        json.put("senderName", senderName)
        json.put("recipientNodeId", recipientNodeId ?: "")
        json.put("chatId", chatId ?: "")
        json.put("messageId", messageId ?: "")
        json.put("encryptedContent", encryptedContent ?: "")
        json.put("pqcPublicKey", pqcPublicKey ?: "")
        json.put("timestamp", timestamp)
        json.put("signature", signature ?: "")
        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): P2pEnvelope? {
            return try {
                val json = JSONObject(jsonStr)
                val typeName = json.optString("type", P2pMessageType.DIRECT_MESSAGE.name)
                val msgType = try { P2pMessageType.valueOf(typeName) } catch (e: Exception) { P2pMessageType.DIRECT_MESSAGE }
                P2pEnvelope(
                    type = msgType,
                    senderNodeId = json.optString("senderNodeId", ""),
                    senderName = json.optString("senderName", "Unknown Node"),
                    recipientNodeId = json.optString("recipientNodeId").takeIf { it.isNotBlank() },
                    chatId = json.optString("chatId").takeIf { it.isNotBlank() },
                    messageId = json.optString("messageId").takeIf { it.isNotBlank() },
                    encryptedContent = json.optString("encryptedContent").takeIf { it.isNotBlank() },
                    pqcPublicKey = json.optString("pqcPublicKey").takeIf { it.isNotBlank() },
                    timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                    signature = json.optString("signature").takeIf { it.isNotBlank() }
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
