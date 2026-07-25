package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ExportBackupDialog(
    onDismiss: () -> Unit,
    onGenerateBackup: (String) -> Unit
) {
    var passphrase by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = QuantumCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Encrypted Zip Vault", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Generates a zero-knowledge downloadable .qpkg / .zip encrypted archive wrapped with AES-256-GCM + Kyber-1024.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text("Master Recovery Passphrase", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate
                    ),
                    modifier = Modifier.testTag("input_backup_passphrase")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (passphrase.isNotBlank()) {
                        onGenerateBackup(passphrase)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_export_backup")
            ) {
                Text("Generate & Save Zip", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSlate
    )
}
