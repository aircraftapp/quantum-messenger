package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String, // Node Public Fingerprint ID
    val name: String,
    val phoneNumber: String = "",
    val pqcPublicKey: String,
    val verifiedFingerprint: String,
    val isOnline: Boolean = true,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)

