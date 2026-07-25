package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isGroup: Boolean = false,
    val participantIdsCsv: String, // Comma-separated participant node IDs
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val ephemeralSettingSeconds: Long = 0L, // Default auto-deletion timer for this chat (0 = Off)
    val pqcHandshakeVerified: Boolean = true,
    val securityFingerprint: String = "PQC-8F92-4A71-BC39",
    val avatarUrl: String? = null,
    val wallpaperTheme: String = "DARK_SLATE", // DARK_SLATE, MATRIX_GREEN, CYBERPUNK, MIDNIGHT_AURORA, SUNSET_GOLD
    val isChannel: Boolean = false, // Telegram-style broadcast channel
    val channelSubscriberCount: Int = 0,
    val isArchived: Boolean = false,
    val draftText: String = ""
)
