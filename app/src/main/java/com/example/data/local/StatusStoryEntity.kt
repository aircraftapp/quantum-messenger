package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_stories")
data class StatusStoryEntity(
    @PrimaryKey
    val id: String,
    val authorId: String,
    val authorName: String,
    val caption: String,
    val mediaUri: String? = null,
    val bgGradientHex: String = "#0A0E17",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAtTimestamp: Long = System.currentTimeMillis() + 86400000L // 24 hours
)
