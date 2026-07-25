package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CloudProviderType {
    GOOGLE_DRIVE, DOWNLOADABLE_ZIP, WEBDAV, LOCAL_VAULT
}

@Entity(tableName = "cloud_accounts")
data class CloudAccountEntity(
    @PrimaryKey
    val id: String,
    val providerName: String,
    val providerType: CloudProviderType,
    val accountEmailOrPath: String,
    val isConnected: Boolean = true,
    val isAutoSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val totalBackupsCount: Int = 1,
    val storageUsedFormatted: String = "4.2 MB"
)
