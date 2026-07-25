package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EnterpriseMdmScreen(
    isEnterpriseMode: Boolean,
    isTorEnabled: Boolean,
    torCircuitStatus: String,
    torOnionAddress: String,
    isScreenshotPreventionEnabled: Boolean,
    isClipboardIsolationEnabled: Boolean,
    isRemoteWipeConfigured: Boolean,
    deadManSwitchDays: Int,
    statusMessage: String,
    onBackClick: () -> Unit,
    onToggleEnterpriseMode: (Boolean) -> Unit,
    onToggleTorRouting: (Boolean) -> Unit,
    onToggleScreenshotPrevention: (Boolean) -> Unit,
    onToggleClipboardIsolation: (Boolean) -> Unit,
    onUpdateDeadManSwitchDays: (Int) -> Unit,
    onTriggerRemoteWipe: () -> Unit
) {
    var showWipeConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // --- TOP APP BAR ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSlate,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "←",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantumCyan,
                        modifier = Modifier
                            .clickable { onBackClick() }
                            .padding(4.dp)
                            .testTag("btn_mdm_back")
                    )

                    Column {
                        Text(
                            text = "ENTERPRISE MDM POLICY ENGINE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Zero-Trust Device Compliance & Anti-Forensic Defense",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    color = TacticalEmerald.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald)
                ) {
                    Text(
                        text = if (isEnterpriseMode) "ENTERPRISE TIER" else "COMMUNITY TIER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        if (statusMessage.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = InnerBoxSlate
            ) {
                Text(
                    text = statusMessage,
                    fontSize = 11.sp,
                    color = QuantumCyan,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // --- CONTENT BODY ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Edition Switcher
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🏢 TIER & LICENSE MANAGEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalEmerald,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enterprise Defender Tier", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Enables central MDM compliance, anti-forensics, and DLP security policies", fontSize = 11.sp, color = TextMuted)
                        }

                        Switch(
                            checked = isEnterpriseMode,
                            onCheckedChange = onToggleEnterpriseMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBlack,
                                checkedTrackColor = TacticalEmerald,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = InnerBoxSlate
                            ),
                            modifier = Modifier.testTag("switch_enterprise_mode")
                        )
                    }
                }
            }

            // Section 2: Tor Onion Routing Integration
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🧅 TOR ONION ROUTING LAYER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber, letterSpacing = 1.sp)
                            Text("Route all P2P post-quantum signals through Tor SOCKS5 proxy to mask IP addresses from state-level surveillance", fontSize = 11.sp, color = TextMuted)
                        }

                        Switch(
                            checked = isTorEnabled,
                            onCheckedChange = onToggleTorRouting,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBlack,
                                checkedTrackColor = WarningAmber,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = InnerBoxSlate
                            ),
                            modifier = Modifier.testTag("switch_mdm_tor")
                        )
                    }

                    Surface(
                        color = InnerBoxSlate,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("SOCKS5 Proxy Endpoint: 127.0.0.1:9050", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
                            Text("Hidden Service: $torOnionAddress", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = QuantumCyan)
                            Text("Circuit State: $torCircuitStatus", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isTorEnabled) TacticalEmerald else AlertCrimson)
                        }
                    }
                }
            }

            // Section 3: Data Loss Prevention (DLP) Policies
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🛡️ DATA LOSS PREVENTION (DLP)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantumCyan,
                        letterSpacing = 1.sp
                    )

                    // Screenshot Prevention
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Block Screenshots & Recording", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Enforces FLAG_SECURE on all compose window surfaces", fontSize = 10.sp, color = TextMuted)
                        }

                        Switch(
                            checked = isScreenshotPreventionEnabled,
                            onCheckedChange = onToggleScreenshotPrevention,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBlack,
                                checkedTrackColor = QuantumCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = InnerBoxSlate
                            ),
                            modifier = Modifier.testTag("switch_screenshot_prevention")
                        )
                    }

                    Divider(color = BorderSlate)

                    // Clipboard Isolation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clipboard Isolation Policy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Prevents copying sensitive decrypted messages to external apps", fontSize = 10.sp, color = TextMuted)
                        }

                        Switch(
                            checked = isClipboardIsolationEnabled,
                            onCheckedChange = onToggleClipboardIsolation,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBlack,
                                checkedTrackColor = QuantumCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = InnerBoxSlate
                            ),
                            modifier = Modifier.testTag("switch_clipboard_isolation")
                        )
                    }
                }
            }

            // Section 4: Anti-Forensics & Remote Wipe
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🚨 ANTI-FORENSICS & EMERGENCY REMOTE WIPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertCrimson,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Dead Man's Inactivity Switch automatically purges all Room SQLite databases, Kyber private keys, and cached media files if the application is not unlocked within the threshold.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    // Dead man switch selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dead Man's Switch Threshold:", fontSize = 11.sp, color = TextPrimary)

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1, 3, 7, 14, 30).forEach { days ->
                                val isSelected = deadManSwitchDays == days
                                Surface(
                                    color = if (isSelected) AlertCrimson else InnerBoxSlate,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clickable { onUpdateDeadManSwitchDays(days) }
                                        .testTag("deadman_$days")
                                ) {
                                    Text(
                                        text = "${days}d",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { showWipeConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_trigger_remote_wipe")
                    ) {
                        Text("🚨 Trigger Instant Remote Wipe (Simulated)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        }

        // Wipe Confirmation Dialog
        if (showWipeConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showWipeConfirmDialog = false },
                title = {
                    Text("🚨 CONFIRM EMERGENCY REMOTE WIPE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlertCrimson)
                },
                text = {
                    Text(
                        "Are you sure you want to trigger a zeroize wipe? This will permanently erase all PQC key pairs, Room database contents, and conversation histories.",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showWipeConfirmDialog = false
                            onTriggerRemoteWipe()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson)
                    ) {
                        Text("PURGE ALL DATA NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWipeConfirmDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                },
                containerColor = CardSlate
            )
        }
    }
}
