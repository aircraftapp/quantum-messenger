package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatEntity
import com.example.ui.theme.*

@Composable
fun ChatSettingsDialog(
    chat: ChatEntity,
    onDismiss: () -> Unit,
    onEphemeralTimerChange: (Long) -> Unit,
    onWallpaperThemeChange: (String) -> Unit
) {
    val timerOptions = listOf(
        0L to "Off (Persistent)",
        10L to "10 Seconds",
        60L to "1 Minute",
        3600L to "1 Hour",
        86400L to "24 Hours",
        604800L to "7 Days"
    )

    val wallpaperThemes = listOf(
        "DARK_SLATE" to Pair("Tactical Dark", DarkSlate),
        "MATRIX_GREEN" to Pair("Matrix Grid", Color(0xFF003B00)),
        "CYBERPUNK" to Pair("Cyber Cyan", Color(0xFF0F2027)),
        "MIDNIGHT_AURORA" to Pair("Midnight Aurora", Color(0xFF130F40)),
        "SUNSET_GOLD" to Pair("Amber Gold", Color(0xFF2C1A04))
    )

    var currentTimer by remember { mutableStateOf(chat.ephemeralSettingSeconds) }
    var currentTheme by remember { mutableStateOf(chat.wallpaperTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = QuantumCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${chat.title} Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Ephemeral Deletion Timer
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = AlertCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EPHEMERAL MESSAGE SHREDDER TIMER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertCrimson,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Messages sent in this chat will auto-shred after being read.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        timerOptions.forEach { (seconds, label) ->
                            val isSelected = currentTimer == seconds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AlertCrimson.copy(alpha = 0.2f) else InnerBoxSlate)
                                    .border(1.dp, if (isSelected) AlertCrimson else BorderSlate, RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentTimer = seconds
                                        onEphemeralTimerChange(seconds)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("option_ephemeral_$seconds"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, fontSize = 12.sp, color = TextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AlertCrimson, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Divider(color = BorderSlate)

                // Section 2: Custom Chat Wallpaper & Theme
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHAT THEME & WALLPAPER TEXTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wallpaperThemes.forEach { (themeKey, pair) ->
                            val (themeLabel, bgCol) = pair
                            val isSelected = currentTheme == themeKey

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgCol)
                                    .border(2.dp, if (isSelected) QuantumCyan else BorderSlate, RoundedCornerShape(10.dp))
                                    .clickable {
                                        currentTheme = themeKey
                                        onWallpaperThemeChange(themeKey)
                                    }
                                    .testTag("theme_option_$themeKey"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                // Security Fingerprint Details
                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "PQC FINGERPRINT VERIFICATION", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = chat.securityFingerprint,
                            fontSize = 12.sp,
                            color = TacticalEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_close_chat_settings")
            ) {
                Text("Save & Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSlate,
        shape = RoundedCornerShape(20.dp)
    )
}
