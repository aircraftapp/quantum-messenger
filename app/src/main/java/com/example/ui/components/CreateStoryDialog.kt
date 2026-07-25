package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CreateStoryDialog(
    onDismiss: () -> Unit,
    onPostStory: (caption: String, bgGradientHex: String) -> Unit
) {
    var captionText by remember { mutableStateOf("") }
    var selectedGradientHex by remember { mutableStateOf("#0D1B2A") }

    val gradientOptions = listOf(
        "#0D1B2A" to DarkSlate,
        "#1B263B" to Color(0xFF1B263B),
        "#4A1525" to Color(0xFF4A1525),
        "#0F2027" to Color(0xFF0F2027),
        "#1F4037" to Color(0xFF1F4037)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TacticalEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post Encrypted Status Story", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Encrypted status updates expire automatically after 24 hours and are visible only to verified PQC contacts.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    placeholder = { Text("What's on your mind?", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_story_caption"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    )
                )

                Text(text = "BACKGROUND CANVAS THEME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gradientOptions.forEach { (hex, col) ->
                        val isSelected = selectedGradientHex == hex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(2.dp, if (isSelected) TacticalEmerald else BorderSlate, CircleShape)
                                .clickable { selectedGradientHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (captionText.isNotBlank()) {
                        onPostStory(captionText, selectedGradientHex)
                    }
                },
                enabled = captionText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_confirm_post_story")
            ) {
                Text("Post 24h Story", fontWeight = FontWeight.Bold)
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
