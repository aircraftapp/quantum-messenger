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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatEntity
import com.example.ui.theme.*

@Composable
fun BroadcastListDialog(
    chats: List<ChatEntity>,
    onDismiss: () -> Unit,
    onSendBroadcast: (selectedChatIds: List<String>, messageText: String) -> Unit
) {
    var broadcastText by remember { mutableStateOf("") }
    val selectedChatIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Send, contentDescription = null, tint = QuantumCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Encrypted Broadcast", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Broadcast sends a single post-quantum encrypted message to multiple individual contacts simultaneously.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = broadcastText,
                    onValueChange = { broadcastText = it },
                    placeholder = { Text("Enter broadcast message...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("input_broadcast_text"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    )
                )

                Text(
                    text = "SELECT RECIPIENT CHATS (${selectedChatIds.size} selected)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chats, key = { it.id }) { chat ->
                        val isSelected = selectedChatIds.contains(chat.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkSlate else InnerBoxSlate)
                                .border(1.dp, if (isSelected) QuantumCyan else BorderSlate, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isSelected) selectedChatIds.remove(chat.id) else selectedChatIds.add(chat.id)
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) QuantumCyan else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = chat.title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (broadcastText.isNotBlank() && selectedChatIds.isNotEmpty()) {
                        onSendBroadcast(selectedChatIds.toList(), broadcastText)
                    }
                },
                enabled = broadcastText.isNotBlank() && selectedChatIds.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_send_broadcast")
            ) {
                Text("Send Broadcast", fontWeight = FontWeight.Bold)
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
