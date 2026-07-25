package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ImportBackupDialog(
    backupFiles: List<File>,
    onDismiss: () -> Unit,
    onImportBackup: (selectedFile: File, passphrase: String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<File?>(backupFiles.firstOrNull()) }
    var passphrase by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FolderZip, contentDescription = null, tint = QuantumCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore Encrypted Zip Vault", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Select a local password-protected .zip / .qpkg vault archive to decrypt and restore into your local Room database.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                if (backupFiles.isEmpty()) {
                    Surface(
                        color = InnerBoxSlate,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No local .zip backup archives found in device storage. Export a backup first or place a .zip file in app backups.",
                            fontSize = 12.sp,
                            color = WarningAmber,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Text(
                        text = "LOCAL ZIP ARCHIVE FILES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(backupFiles, key = { it.absolutePath }) { file ->
                            val isSelected = selectedFile?.absolutePath == file.absolutePath
                            val formattedDate = dateFormat.format(Date(file.lastModified()))
                            val sizeKb = file.length() / 1024

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DarkSlate else InnerBoxSlate)
                                    .border(
                                        1.dp,
                                        if (isSelected) QuantumCyan else BorderSlate,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedFile = file }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.FolderZip,
                                    contentDescription = null,
                                    tint = if (isSelected) QuantumCyan else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "$formattedDate • $sizeKb KB",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text("Vault Encryption Passphrase", color = TextMuted) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = QuantumCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_import_passphrase")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fileToRestore = selectedFile
                    if (fileToRestore != null && passphrase.isNotBlank()) {
                        onImportBackup(fileToRestore, passphrase)
                    }
                },
                enabled = selectedFile != null && passphrase.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_import_backup")
            ) {
                Text("Decrypt & Restore Database", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSlate,
        shape = RoundedCornerShape(20.dp)
    )
}
