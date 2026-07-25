package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
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
fun CreateChannelDialog(
    onDismiss: () -> Unit,
    onCreateChannel: (channelName: String, description: String, isPublic: Boolean) -> Unit
) {
    var channelName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = TacticalEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Telegram-Style Channel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Channels are read-only notification streams where you can broadcast announcements to unlimited subscribers.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("Channel Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_channel_name")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Purpose", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    ),
                    modifier = Modifier.fillMaxWidth().height(80.dp).testTag("input_channel_description")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Public Directory Channel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Discoverable by peer nodes", fontSize = 10.sp, color = TextMuted)
                    }
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = TacticalEmerald
                        ),
                        modifier = Modifier.testTag("switch_public_channel")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (channelName.isNotBlank()) {
                        onCreateChannel(channelName, description, isPublic)
                    }
                },
                enabled = channelName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_create_channel")
            ) {
                Text("Create Channel", fontWeight = FontWeight.Bold)
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
