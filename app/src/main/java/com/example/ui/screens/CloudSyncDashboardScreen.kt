package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CloudAccountEntity
import com.example.data.local.CloudProviderType
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import com.example.crypto.QuantumCryptoEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncDashboardScreen(
    cloudAccounts: List<CloudAccountEntity>,
    localBackupFiles: List<File>,
    statusMessage: String?,
    sharedFolders: List<QuantumCryptoEngine.SharedFolderSyncItem> = emptyList(),
    onBackClick: () -> Unit,
    onSyncAccountClick: (CloudAccountEntity) -> Unit,
    onDeleteAccountClick: (CloudAccountEntity) -> Unit,
    onExportZipClick: () -> Unit,
    onImportZipClick: () -> Unit,
    onAddCloudAccountClick: (String, CloudProviderType, String) -> Unit,
    onCreateSharedFolder: (String, String) -> Unit = { _, _ -> },
    onSyncSharedFolder: (String) -> Unit = {}
) {
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf("") }
    var sharedPeerInput by remember { mutableStateOf("") }
    var accountNameInput by remember { mutableStateOf("") }
    var accountEmailOrPathInput by remember { mutableStateOf("") }
    var selectedProviderType by remember { mutableStateOf(CloudProviderType.GOOGLE_DRIVE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Encrypted Cloud Sync Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_cloud_dashboard")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlate)
            )
        },
        containerColor = ObsidianBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hero Banner Asset
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_security_banner_1784846382776),
                    contentDescription = "Cloud Security Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    ObsidianBlack
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = TacticalEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zero-Knowledge Cross-Platform Vault Sync",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            if (statusMessage != null) {
                Surface(
                    color = TacticalEmerald.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $statusMessage",
                        color = TacticalEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Quick Actions Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExportZipClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_zip_vault"),
                        colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Export .zip Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onImportZipClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_import_zip_vault"),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Restore .zip Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAddAccountDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_add_storage_account"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = QuantumCyan),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuantumCyan)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Connect Cloud", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showCreateFolderDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_share_folder_pqc"),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderShared, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "+ Shared Folder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section 0: PQC Shared Synchronized Folders
            if (sharedFolders.isNotEmpty()) {
                Text(
                    text = "P2P PQC SHARED FOLDERS (LIVE AUTO-SYNC WHEN ONLINE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sharedFolders, key = { it.id }) { sf ->
                        Surface(
                            color = CardSlate,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (sf.isOnline) TacticalEmerald.copy(alpha = 0.8f) else BorderSlate),
                            modifier = Modifier.fillMaxWidth().testTag("shared_folder_card_${sf.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = sf.folderName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = "Shared with: ${sf.sharedWithPeer} • ${sf.fileCount} files (${sf.totalSizeFormatted})",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onSyncSharedFolder(sf.id) },
                                        enabled = !sf.isSyncing,
                                        colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(text = if (sf.isSyncing) "Syncing..." else "Sync", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (sf.isSyncing || sf.syncProgress < 1.0f) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { sf.syncProgress },
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = TacticalEmerald,
                                        trackColor = InnerBoxSlate
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Section 1: Local Encrypted Zip Archives on Device
            if (localBackupFiles.isNotEmpty()) {
                Text(
                    text = "LOCAL ENCRYPTED ZIP VAULTS ON DEVICE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 130.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(localBackupFiles, key = { it.absolutePath }) { backupFile ->
                        Surface(
                            color = InnerBoxSlate,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = backupFile.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                    Text(text = "${backupFile.length() / 1024} KB • PBKDF2-AES256-GCM Encrypted", fontSize = 10.sp, color = TextMuted)
                                }
                                TextButton(
                                    onClick = onImportZipClick,
                                    colors = ButtonDefaults.textButtonColors(contentColor = TacticalEmerald)
                                ) {
                                    Text("Restore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "CONNECTED STORAGE ACCOUNTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cloudAccounts, key = { it.id }) { account ->
                    CloudAccountCard(
                        account = account,
                        onSyncNow = { onSyncAccountClick(account) },
                        onDelete = { onDeleteAccountClick(account) }
                    )
                }
            }
        }
    }

    // Add Cloud Account Dialog
    if (showAddAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            title = {
                Text(
                    text = "Connect Cloud Storage Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "All sync packages are locally encrypted with AES-256-GCM + Kyber-1024 before uploading to your cloud.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = accountNameInput,
                        onValueChange = { accountNameInput = it },
                        label = { Text("Account Label (e.g. My Google Drive)", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = QuantumCyan,
                            unfocusedBorderColor = BorderSlate
                        )
                    )

                    OutlinedTextField(
                        value = accountEmailOrPathInput,
                        onValueChange = { accountEmailOrPathInput = it },
                        label = { Text("Cloud Account Email or WebDAV URL", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = QuantumCyan,
                            unfocusedBorderColor = BorderSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (accountNameInput.isNotBlank()) {
                            onAddCloudAccountClick(
                                accountNameInput,
                                selectedProviderType,
                                if (accountEmailOrPathInput.isBlank()) "user.cloud@storage.org" else accountEmailOrPathInput
                            )
                            showAddAccountDialog = false
                            accountNameInput = ""
                            accountEmailOrPathInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack)
                ) {
                    Text("Connect & Encrypt", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAccountDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSlate
        )
    }

    // Create PQC Shared Folder Dialog
    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = {
                Text(
                    text = "Create PQC Shared Sync Folder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Specify a folder path and peer/group to automatically sync files with Kyber-1024 encryption when online.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        label = { Text("Folder Name (e.g. Field_Intel_Vault)", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = WarningAmber,
                            unfocusedBorderColor = BorderSlate
                        )
                    )

                    OutlinedTextField(
                        value = sharedPeerInput,
                        onValueChange = { sharedPeerInput = it },
                        label = { Text("Peer Node or Group Name", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = WarningAmber,
                            unfocusedBorderColor = BorderSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (folderNameInput.isNotBlank()) {
                            onCreateSharedFolder(folderNameInput, sharedPeerInput)
                            showCreateFolderDialog = false
                            folderNameInput = ""
                            sharedPeerInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = ObsidianBlack)
                ) {
                    Text("Create & Auto-Sync", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSlate
        )
    }
}

@Composable
private fun CloudAccountCard(
    account: CloudAccountEntity,
    onSyncNow: () -> Unit,
    onDelete: () -> Unit
) {
    val syncTimeFormatted = remember(account.lastSyncTimestamp) {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        sdf.format(Date(account.lastSyncTimestamp))
    }

    val iconVector = when (account.providerType) {
        CloudProviderType.GOOGLE_DRIVE -> Icons.Default.CloudQueue
        CloudProviderType.DOWNLOADABLE_ZIP -> Icons.Default.FolderZip
        CloudProviderType.WEBDAV -> Icons.Default.Dns
        CloudProviderType.LOCAL_VAULT -> Icons.Default.VpnKey
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cloud_account_card_${account.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardSlate)
                        .border(1.dp, QuantumCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = iconVector, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.providerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = account.accountEmailOrPath,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = AlertCrimson)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Last Zero-Knowledge Sync", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = syncTimeFormatted,
                        fontSize = 12.sp,
                        color = TacticalEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onSyncNow,
                    colors = ButtonDefaults.buttonColors(containerColor = CardSlate, contentColor = QuantumCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
