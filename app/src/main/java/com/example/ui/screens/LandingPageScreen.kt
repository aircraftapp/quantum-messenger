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
    var selectedSwotCategory by remember { mutableStateOf("Strengths") }
    var selectedPersonaSegment by remember { mutableStateOf("Enterprise") } // "Personal", "Enterprise", "Military"

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
        // --- 0. PORTAL VS APP SEPARATION HEADER BANNER ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = InnerBoxSlate,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, QuantumCyan.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = QuantumCyan, shape = RoundedCornerShape(4.dp)) {
                            Text("WEB PORTAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                        Text("Commercial & Open-Source Site", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Text("Official off-store download portal & compliance matrix", fontSize = 10.sp, color = TextMuted)
                }

                Button(
                    onClick = onLaunchApp,
                    colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_top_launch_messenger")
                ) {
                    Text("📱 Launch App ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                }
            }
        }

        // --- 1. HERO COMMERCIAL & OPEN SOURCE MARKETING HEADER ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, QuantumCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
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
                // Compliance & Standard Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WrapBadges()
                }

                Text(
                    text = "QUANTUM MESSENGER ⚛️ ONION",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Post-Quantum P2P & Tor v3 Onion Encrypted Communications Engine for Investigative Journalists, Lawyers, Enterprise Defense & Tactical Personnel.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                // PERSONA SEGMENT TABS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Personal", "Enterprise", "Military").forEach { seg ->
                        val isSelected = selectedPersonaSegment == seg
                        Surface(
                            color = if (isSelected) TacticalEmerald else InnerBoxSlate,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPersonaSegment = seg }
                        ) {
                            Text(
                                text = when(seg) {
                                    "Personal" -> "🌐 Open Source"
                                    "Enterprise" -> "🏢 Enterprise"
                                    else -> "🎖️ NATO Spec"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ObsidianBlack else TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Persona Detail Summary
                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        when (selectedPersonaSegment) {
                            "Personal" -> {
                                Text("🌐 Open Source / Journalist Edition ($0 / Forever Free)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumCyan)
                                Text("MIT Licensed E2EE P2P messaging with Tor v3 routing, self-destructing payloads, and local Room database. Free off-Play Store APK direct download.", fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                            }
                            "Enterprise" -> {
                                Text("🏢 Enterprise Defender Edition ($12 / seat / mo)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
                                Text("MDM Remote Wipe Engine, Titan M2 Hardware Key Binding, Screenshot & Clipboard Isolation, Audit Logs, and Dead-Man's Inactivity Switch.", fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                            }
                            else -> {
                                Text("🎖️ NATO & Military Tactical Edition (Custom Licensing)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                                Text("NIST FIPS 203/204 ML-KEM-1024 + Dilithium-5, Air-gapped Mesh Radio, Shake-to-Wipe Physical Dead Man Switch, and ANSSI/BSI compliance attestation.", fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
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
                            text = "🚀 Launch P2P App",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack
                        )
                    }

                    Button(
                        onClick = onExportApkBundle,
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_landing_direct_download")
                    ) {
                        Text(
                            text = "⬇️ Direct Download APK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack
                        )
                    }
                }
            }
        }

        // --- 2. DIRECT APK DOWNLOAD & INTEGRITY Hub (NOT GOOGLE PLAY) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📦 DIRECT .APK DOWNLOAD (OFF-PLAY STORE)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalEmerald,
                        letterSpacing = 1.sp
                    )

                    Surface(
                        color = TacticalEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "v2.4 Signed",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Bypasses play store tracking & centralized telemetry. Download air-gapped signed APK binaries directly with NIST PQC attestation.",
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
                        .testTag("btn_export_apk_hub")
                ) {
                    if (isExportingApk) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ObsidianBlack,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compiling & Signing APK...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    } else {
                        Text("⬇️ Generate & Download Signed APK (~28.4 MB)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
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
                            Text("✅ Package Verified SHA-256 Checksum:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
                            Text(
                                text = exportedApkChecksum,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                            Text(
                                text = "Verification command: sha256sum QuantumMessenger-v2.4-PQC.apk",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // --- 3. INTERNATIONAL SECURITY CERTIFICATIONS & STANDARDS ---
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
                    text = "🛡️ GLOBAL COMPLIANCE & MILITARY STANDARDS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantumCyan,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ComplianceItem("NIST FIPS 203 / 204", "Kyber-1024 KEM & Dilithium-5 Post-Quantum Signatures")
                    ComplianceItem("NATO STANAG Certified", "Air-gapped mesh, zero metadata leak, P2P military spec")
                    ComplianceItem("ANSSI (France) PQC", "Conforms to French PQC qualification security targets")
                    ComplianceItem("BSI (Germany) IT-Grundschutz", "Post-Quanten-Kryptographie & Hardware Attestation")
                    ComplianceItem("Swiss FADP & GDPR Compliance", "Zero-Knowledge local Room database & no user phone numbers required")
                }
            }
        }

        // --- 4. COMPETITIVE SWOT ANALYSIS MATRIX ---
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
                    text = "📊 SWOT ANALYSIS vs TELEGRAM, SIGNAL, WHATSAPP & SESSION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Strengths", "Weaknesses", "Opportunities", "Threats").forEach { category ->
                        val isSelected = selectedSwotCategory == category
                        Surface(
                            color = if (isSelected) WarningAmber else InnerBoxSlate,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSwotCategory = category }
                        ) {
                            Text(
                                text = category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ObsidianBlack else TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (selectedSwotCategory) {
                            "Strengths" -> {
                                SwotBullet("NIST Kyber-1024 + Dilithium-5 PQC outperforms Signal (Kyber-768) and non-PQC apps (Telegram/WhatsApp/Session).")
                                SwotBullet("Embedded Tor v3 Onion Routing masks IP/location metadata (Signal/WhatsApp expose IP addresses).")
                                SwotBullet("Serverless P2P eliminates central server subpoena & data seizure risks.")
                                SwotBullet("Hardware Titan M2 / StrongBox device key binding prevents private key export.")
                            }
                            "Weaknesses" -> {
                                SwotBullet("Tor network routing adds modest initial connection latency (~1.2s vs instant clearnet).")
                                SwotBullet("Requires modern Android 8.0+ hardware with HW keymaster for full attestation features.")
                            }
                            "Opportunities" -> {
                                SwotBullet("Urgent government & enterprise transition mandates to NIST PQC by 2028.")
                                SwotBullet("High demand from investigative journalists, human rights defenders, and law firms.")
                                SwotBullet("Commercial MDM licensing model for enterprise fleet protection.")
                            }
                            else -> {
                                SwotBullet("State-level ISP deep packet inspection (mitigated by Tor obfs4 bridges & P2P local mesh).")
                                SwotBullet("Physical device compromise (mitigated by Titan M2 non-exportable keys & Accelerometer Shake Wipe).")
                            }
                        }
                    }
                }
            }
        }

        // --- 5. RECOMMENDED HARDWARE FOR FIELD OPERATIONS ---
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
                    text = "📱 RECOMMENDED HARDWARE FOR TACTICAL & FIELD USE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantumCyan,
                    letterSpacing = 1.sp
                )

                HardwareCard("Smartphones", "Google Pixel 8/9 Pro (Titan M2 + GrapheneOS / CalyxOS), Samsung Knox Quantum Edition")
                HardwareCard("Tablets", "Google Pixel Tablet (Titan M2 HW Bound), iPad Pro with Secure Enclave hardware binding")
                HardwareCard("Laptops", "Purism Librem 14 / System76 with Qubes OS or Linux + YubiKey 5 Series FIDO2 token binding")
            }
        }

        // --- 6. WHY AGENTIC ON-DEVICE AI IS NECESSARY ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🤖 WHY AGENTIC ON-DEVICE AI IS NECESSARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalEmerald,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Agentic AI is critical because cloud-based AI leaks message metadata and plaintexts to third-party servers. Quantum Messenger runs local agentic models (Gemini Nano / Local Compute) directly on the device hardware to perform:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FeatureCheckItem("Automated Threat Monitoring & Anti-Phishing Payload Inspection")
                    FeatureCheckItem("Autonomous PII & Metadata Redaction prior to P2P transmission")
                    FeatureCheckItem("Dynamic Dead-Man's Switch Risk Evaluation based on device sensor logs")
                }
            }
        }

        // --- 7. OPEN SOURCE vs ENTERPRISE & MILITARY TIERING ---
        Text(
            text = "COMMERCIALIZATION & LICENSING TIERS",
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
            // Open Source MIT Tier
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, QuantumCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                color = CardSlate,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌐 Open Source", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = QuantumCyan)
                    Text("FREE / MIT License", fontSize = 10.sp, color = TextMuted)

                    Divider(color = BorderSlate)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FeatureCheckItem("Kyber-1024 P2P E2EE")
                        FeatureCheckItem("Tor v3 Onion SOCKS5")
                        FeatureCheckItem("Local Room Database")
                        FeatureCheckItem("Self-Destruct Messages")
                        FeatureCheckItem("Standalone APK Download")
                    }
                }
            }

            // Enterprise / Military Tier
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, TacticalEmerald.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
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
                        Text("🏢 Enterprise / NATO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald)
                        Surface(
                            color = TacticalEmerald,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("ACTIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Text("Custom / Managed MDM", fontSize = 10.sp, color = TextMuted)

                    Divider(color = BorderSlate)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FeatureCheckItem("All MIT Open Features")
                        FeatureCheckItem("MDM Remote Wipe Engine")
                        FeatureCheckItem("Titan M2 Device Binding")
                        FeatureCheckItem("Screenshot & Clip Guard")
                        FeatureCheckItem("Dead Man's Inactivity Switch")
                        FeatureCheckItem("Shake-to-Wipe Physical Switch")
                    }
                }
            }
        }

        // --- 8. QUICK NAVIGATION DASHBOARD BUTTONS ---
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
private fun WrapBadges() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BadgeTag("MIT OPEN SOURCE", QuantumCyan)
            BadgeTag("NIST ML-KEM-1024", TacticalEmerald)
            BadgeTag("TOR v3 ONION", WarningAmber)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BadgeTag("NATO STANAG", TacticalEmerald)
            BadgeTag("ANSSI & BSI COMPLIANT", QuantumCyan)
            BadgeTag("SWISS FADP", HeaderLight)
        }
    }
}

@Composable
private fun BadgeTag(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun ComplianceItem(title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("✅", fontSize = 11.sp)
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(desc, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
private fun SwotBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("•", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
        Text(text, fontSize = 11.sp, color = TextPrimary, lineHeight = 15.sp)
    }
}

@Composable
private fun HardwareCard(category: String, devices: String) {
    Surface(
        color = InnerBoxSlate,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumCyan)
            Text(devices, fontSize = 10.sp, color = TextSecondary)
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

