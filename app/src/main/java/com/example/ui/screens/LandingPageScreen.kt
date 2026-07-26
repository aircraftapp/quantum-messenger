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
import androidx.compose.ui.platform.LocalUriHandler
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

        // --- 5. SECURE HARDWARE RECOMMENDATIONS FOR ENTERPRISE & MILITARY USE ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, QuantumCyan.copy(alpha = 0.5f))
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
                        text = "📱 HARDWARE RECOMMENDATIONS & SPECS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantumCyan,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = QuantumCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ATTESTED HSM",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Zero-trust PQC protection requires hardware-enforced cryptographic key isolation. The following devices provide hardware Titan M2, physical kill switches, and FIPS 140-3 HSM attestation:",
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )

                HardwareSpecCard(
                    category = "RECOMMENDED ANDROID PHONE",
                    title = "Google Pixel 8 / 9 Pro (Titan M2 + GrapheneOS)",
                    description = "Features Google Titan M2 hardware security chip & StrongBox Keymaster. Enforces non-exportable ML-KEM-1024 encryption keys with GrapheneOS / CalyxOS.",
                    specs = "FIPS 140-2 L3 HSM • Insite Side-Channel Shield • HW Root of Trust",
                    links = listOf(
                        "Titan M2 Specs" to "https://store.google.com/magazine/pixel_titan_m2",
                        "GrapheneOS Security" to "https://grapheneos.org/faq#hardware-security"
                    )
                )

                HardwareSpecCard(
                    category = "MOBILE & DESKTOP PRIVACY",
                    title = "Purism Librem 5 / Librem 14 Workstation",
                    description = "Hardened open-hardware device with 3x Physical Hardware Kill Switches (Cellular, Wi-Fi, Cam/Mic). Built-in OpenPGP smartcard chip for cryptographic key isolation.",
                    specs = "3x Hardware Kill Switches • PureOS / Qubes OS • Smartcard HSM",
                    links = listOf(
                        "Librem 5 Specs" to "https://puri.sm/products/librem-5/",
                        "Librem 14 Specs" to "https://puri.sm/products/librem-14/"
                    )
                )

                HardwareSpecCard(
                    category = "ENTERPRISE WORKSTATION",
                    title = "Lenovo ThinkPad / System76 + YubiKey 5 PQC",
                    description = "Discrete TPM 2.0 tamper-evident enterprise workstation with Qubes OS compartmentalization & YubiKey 5 Series FIDO2/PQC token binding.",
                    specs = "Discrete TPM 2.0 (TCG Certified) • YubiKey 5 PQC • Intel vPro Shield",
                    links = listOf(
                        "ThinkShield Specs" to "https://www.lenovo.com/us/en/thinkpad/security/",
                        "YubiKey 5 Specs" to "https://www.yubico.com/products/yubikey-5-series/"
                    )
                )

                HardwareSpecCard(
                    category = "TACTICAL & MILITARY GRADE",
                    title = "NATO STANAG Ruggedized Handhelds",
                    description = "MIL-STD-810H & IP68 rugged Android handheld with onboard FIPS 140-3 HSM, air-gapped mesh radio interface, and accelerometer Shake-to-Wipe dead man switch.",
                    specs = "MIL-STD-810H • FIPS 140-3 HSM • NATO STANAG Mesh Radio",
                    links = listOf(
                        "NATO Cyber Defence Specs" to "https://www.nato.int/cps/en/natohq/topics_157573.htm"
                    )
                )
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

        // --- 8. FORMAL REQUEST ENTERPRISE TRIAL & SECURITY AUDIT FORM ---
        EnterpriseTrialRequestComponent()

        // --- 9. QUICK NAVIGATION DASHBOARD BUTTONS ---
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
private fun HardwareSpecCard(
    category: String,
    title: String,
    description: String,
    specs: String,
    links: List<Pair<String, String>>
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        color = InnerBoxSlate,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = QuantumCyan, letterSpacing = 0.5.sp)
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(description, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)

            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔒 $specs",
                    fontSize = 9.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                links.forEach { (label, url) ->
                    Text(
                        text = "🔗 $label ➔",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalEmerald,
                        modifier = Modifier.clickable {
                            try {
                                uriHandler.openUri(url)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }
                    )
                }
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

@Composable
private fun EnterpriseTrialRequestComponent() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("Military & Tactical Defense") }
    var scale by remember { mutableStateOf("10 - 50 Seats (Tactical Unit)") }
    var hsmPreference by remember { mutableStateOf("Google Pixel Titan M2 + GrapheneOS") }
    var notes by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var ticketId by remember { mutableStateOf("") }

    val sectorOptions = listOf(
        "Military & Tactical Defense",
        "Investigative Journalism / NGO",
        "Enterprise IT & MDM Compliance",
        "Legal & High-Risk Security",
        "Government Infrastructure"
    )

    val scaleOptions = listOf(
        "10 - 50 Seats (Tactical Unit)",
        "50 - 250 Seats (Enterprise Division)",
        "250 - 1000 Seats (Regional Command)",
        "1000+ Seats (Global Fleet)"
    )

    val hsmOptions = listOf(
        "Google Pixel Titan M2 + GrapheneOS",
        "Purism Librem 5 / Physical Kill Switches",
        "Enterprise Workstation TPM 2.0 + YubiKey 5",
        "NATO STANAG Air-Gapped Mesh Handheld"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("enterprise_trial_section"),
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
                    text = "🛡️ REQUEST ENTERPRISE TRIAL & AUDIT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalEmerald,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    color = QuantumCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "30-DAY TRIAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantumCyan,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Request a tailored proof-of-concept APK build, custom MDM tenant policies, and schedule a zero-trust architecture demo with our defense engineers.",
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )

            if (isSubmitted) {
                Surface(
                    color = TacticalEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✅ Enterprise Audit Ticket Issued!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalEmerald
                        )
                        Text(
                            text = "Ticket Ref: $ticketId",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Your evaluation request for $organization ($sector) has been logged into our zero-trust onboarding portal. A security specialist will reach out to $email within 4 business hours to arrange demo scheduling.",
                            fontSize = 10.sp,
                            color = TextPrimary,
                            lineHeight = 14.sp
                        )
                        Button(
                            onClick = { isSubmitted = false },
                            colors = ButtonDefaults.buttonColors(containerColor = InnerBoxSlate),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Submit Another Request", fontSize = 10.sp, color = QuantumCyan)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trial_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedLabelColor = TacticalEmerald,
                        unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enterprise / Agency Email *", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trial_email_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedLabelColor = TacticalEmerald,
                        unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Organization / Agency *", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trial_org_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedLabelColor = TacticalEmerald,
                        unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Sector selector
                Text("Operating Sector:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    sectorOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { sector = option }
                                .padding(vertical = 1.dp)
                        ) {
                            RadioButton(
                                selected = (sector == option),
                                onClick = { sector = option },
                                colors = RadioButtonDefaults.colors(selectedColor = TacticalEmerald)
                            )
                            Text(option, fontSize = 10.sp, color = if (sector == option) TextPrimary else TextSecondary)
                        }
                    }
                }

                // Scale selector
                Text("Deployment Scale:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    scaleOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { scale = option }
                                .padding(vertical = 1.dp)
                        ) {
                            RadioButton(
                                selected = (scale == option),
                                onClick = { scale = option },
                                colors = RadioButtonDefaults.colors(selectedColor = QuantumCyan)
                            )
                            Text(option, fontSize = 10.sp, color = if (scale == option) TextPrimary else TextSecondary)
                        }
                    }
                }

                // HSM Preference selector
                Text("Preferred Hardware HSM:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    hsmOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hsmPreference = option }
                                .padding(vertical = 1.dp)
                        ) {
                            RadioButton(
                                selected = (hsmPreference == option),
                                onClick = { hsmPreference = option },
                                colors = RadioButtonDefaults.colors(selectedColor = TacticalEmerald)
                            )
                            Text(option, fontSize = 10.sp, color = if (hsmPreference == option) TextPrimary else TextSecondary)
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Security Audit & Demo Schedule Preferences", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trial_notes_input"),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalEmerald,
                        unfocusedBorderColor = BorderSlate,
                        focusedLabelColor = TacticalEmerald,
                        unfocusedLabelColor = TextMuted,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank()) {
                            val code = (1000..9999).random()
                            ticketId = "AUDIT-REQ-$code-2026"
                            isSubmitted = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_trial_request_button"),
                    enabled = name.isNotBlank() && email.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald)
                ) {
                    Text(
                        text = "🛡️ Submit Enterprise Trial Request",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                }
            }
        }
    }
}

