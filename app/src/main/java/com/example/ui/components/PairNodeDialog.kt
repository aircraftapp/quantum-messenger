package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto.QuantumCryptoEngine
import com.example.ui.theme.*

@Composable
fun PairNodeDialog(
    onDismiss: () -> Unit,
    onPairNode: (String, String) -> Unit
) {
    var nodeName by remember { mutableStateOf("") }
    var publicKeyInput by remember { mutableStateOf("") }
    var socketAddressInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = QuantumCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pair New P2P Node",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Your Local Node Public Fingerprint:",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Surface(
                    color = CardSlate,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = QuantumCryptoEngine.devicePqcPublicKey,
                        fontSize = 10.sp,
                        color = TacticalEmerald,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = nodeName,
                    onValueChange = { nodeName = it },
                    label = { Text("Peer Node Name (e.g. Satoshi)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate
                    ),
                    modifier = Modifier.testTag("input_pair_node_name")
                )

                OutlinedTextField(
                    value = publicKeyInput,
                    onValueChange = { publicKeyInput = it },
                    label = { Text("Peer PQC Public Key String", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate
                    ),
                    modifier = Modifier.testTag("input_pair_node_key")
                )

                OutlinedTextField(
                    value = socketAddressInput,
                    onValueChange = { socketAddressInput = it },
                    label = { Text("Direct P2P Socket IP:Port (Optional)", color = TextMuted) },
                    placeholder = { Text("192.168.1.100:8888", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate
                    ),
                    modifier = Modifier.testTag("input_pair_socket_address")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nodeName.isNotBlank()) {
                        onPairNode(nodeName, publicKeyInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_pair_node")
            ) {
                Text("Verify & Pair Node", fontWeight = FontWeight.Bold)
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
