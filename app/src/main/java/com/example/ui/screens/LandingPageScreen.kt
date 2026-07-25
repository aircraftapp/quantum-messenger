package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LandingPageScreen(
    isTorEnabled: Boolean,
    torCircuitStatus: String,
    torOnionAddress: String,
    isEnterpriseMode: Boolean,
    isExportingApk: Boolean,
    exportedApkChecksum: String?,
    onLaunchApp: () -> Unit,
    onOpenEnterpriseMdm: () -> Unit,
    onOpenComputeDashboard: () -> Unit,
    onToggleTorRouting: (Boolean) -> Unit,
    onExportApkBundle: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HERO HEADER ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, QuantumCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            color = DarkSlate,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = QuantumCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚛️ PQC + TOR ONION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = TacticalEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🏢 ENTERPRISE DEFENDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "QUANTUM MESSENGER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Zero-Trust Post-Quantum P2P & Tor Onion Communications Engine for Investigative Journalists, Lawyers & Enterprise Defense",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onLaunchApp,
                        colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_landing_launch_app")
                    ) {
                        Text(
                            text = "🚀 Launch App",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenEnterpriseMdm,
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(TacticalEmerald, QuantumCyan))),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_landing_mdm")
                    ) {
                        Text(
                            text = "🏢 Enterprise MDM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalEmerald
                        )
                    }
                }
            }
        }

        // --- TOR ONION ROUTING STATUS CARD ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isTorEnabled) WarningAmber.copy(alpha = 0.5f) else BorderSlate)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🧅 TOR ONION ROUTING LAYER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber,
                            letterSpacing = 1.sp
                        )
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
                        modifier = Modifier.testTag("switch_tor_routing")
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
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Circuit State:", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = torCircuitStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTorEnabled) TacticalEmerald else AlertCrimson
                            )
                        }

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Onion Address:", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = torOnionAddress,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = QuantumCyan
                            )
                        }

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Transport Payload:", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "NIST Kyber-1024 + Dilithium-5 over Tor SOCKS5",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // --- APK EXPORT & DOWNLOAD SHOWCASE CARD ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📦 OFFICIAL APK DOWNLOAD & CHECKSUM VERIFICATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantumCyan,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Compile or export signed standalone APKs directly from the device with embedded cryptographic attestation for high-threat air-gapped field deployment.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Button(
                    onClick = onExportApkBundle,
                    enabled = !isExportingApk,
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_export_apk")
                ) {
                    if (isExportingApk) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ObsidianBlack,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Packaging APK & Signing...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    } else {
                        Text("⬇️ Generate & Export Standalone .APK Bundle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    }
                }

                if (exportedApkChecksum != null) {
                    Surface(
                        color = InnerBoxSlate,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✅ APK Package Verified & Saved", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
                            Text(
                                text = exportedApkChecksum,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // --- OPEN SOURCE vs ENTERPRISE EDITION MATRIX ---
        Text(
            text = "EDITION & FEATURE COMPARISON",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = HeaderLight,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Community Edition Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌐 Open Source", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = QuantumCyan)
                    Text("Free / AGPL v3 Core", fontSize = 10.sp, color = TextMuted)

                    Divider(color = BorderSlate)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FeatureCheckItem("Kyber-1024 P2P E2EE")
                        FeatureCheckItem("Tor Onion SOCKS5")
                        FeatureCheckItem("Local Room Database")
                        FeatureCheckItem("Air-Gapped Local Compute")
                        FeatureCheckItem("Self-Destruct Messages")
                    }
                }
            }

            // Enterprise Defender Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, TacticalEmerald.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏢 Enterprise", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
                        Surface(
                            color = TacticalEmerald,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("ACTIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Text("For High-Threat & MDM", fontSize = 10.sp, color = TextMuted)

                    Divider(color = BorderSlate)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FeatureCheckItem("All Open Source Features")
                        FeatureCheckItem("MDM Remote Wipe Engine")
                        FeatureCheckItem("Screenshot Prevention")
                        FeatureCheckItem("Clipboard Isolation")
                        FeatureCheckItem("Dead Man's Auto-Wipe")
                        FeatureCheckItem("YubiKey / FIDO2 Hardware")
                    }
                }
            }
        }

        // --- NAVIGATION QUICK LINKS ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = InnerBoxSlate,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💬 Chat List",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantumCyan,
                    modifier = Modifier
                        .clickable { onLaunchApp() }
                        .padding(8.dp)
                        .testTag("link_chat_list")
                )

                Text(
                    text = "⚡ Compute Engine",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalEmerald,
                    modifier = Modifier
                        .clickable { onOpenComputeDashboard() }
                        .padding(8.dp)
                        .testTag("link_compute_engine")
                )

                Text(
                    text = "⚙️ MDM Policy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    modifier = Modifier
                        .clickable { onOpenEnterpriseMdm() }
                        .padding(8.dp)
                        .testTag("link_mdm_policy")
                )
            }
        }
    }
}

@Composable
private fun FeatureCheckItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
        Text(text = text, fontSize = 10.sp, color = TextPrimary)
    }
}
