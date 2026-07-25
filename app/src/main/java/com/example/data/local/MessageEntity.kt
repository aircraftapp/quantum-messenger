package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
    TEXT, VOICE, VIDEO, FILE, WALKIE_TALKIE
}

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val isFromMe: Boolean,
    val textContent: String,
    val encryptedPayload: String, // Base64 Kyber-1024 + AES-GCM ciphertext
    val messageType: MessageType = MessageType.TEXT,
    val mediaUri: String? = null,
    val mediaSizeFormatted: String? = null,
    val mediaDurationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val ephemeralDurationSeconds: Long = 0L, // 0 = Never expire
    val expiresAtTimestamp: Long = 0L, // 0 = No expiration set
    val isRead: Boolean = true,
    val pqcAlgorithm: String = "Kyber-1024 / AES-256-GCM",
    val reactionEmojis: String = ""
)
