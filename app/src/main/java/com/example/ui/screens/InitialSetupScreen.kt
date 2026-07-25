package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto.QuantumCryptoEngine
import com.example.data.local.CloudProviderType
import com.example.ui.theme.*

@Composable
fun InitialSetupScreen(
    currentStep: Int,
    nodeName: String,
    setupPin: String,
    selectedCloudType: CloudProviderType,
    keyGenProgress: Float,
    isGeneratingKeys: Boolean,
    isBiometricEnabled: Boolean = true,
    onNodeNameChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onBiometricToggle: (Boolean) -> Unit = {},
    onCloudTypeSelect: (CloudProviderType) -> Unit,
    onGenerateKeysClick: () -> Unit,
    onNextStepClick: () -> Unit,
    onPreviousStepClick: () -> Unit,
    onCompleteSetupClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianBlack, DarkSlate, ObsidianBlack)
                )
            )
            .padding(20.dp)
            .testTag("initial_setup_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Title & Step Indicator
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Q-CRYPT",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeaderLight,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TacticalEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Initial Setup & Configuration",
                                fontSize = 11.sp,
                                color = TacticalEmerald,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSlate)
                            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Step $currentStep of 5",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Multi-step Progress Bar
                LinearProgressIndicator(
                    progress = { currentStep / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = QuantumCyan,
                    trackColor = CardSlate
                )
            }

            // Step Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    1 -> Step1ArchitectureOverview(
                        keyGenProgress = keyGenProgress,
                        isGeneratingKeys = isGeneratingKeys,
                        onGenerateKeysClick = onGenerateKeysClick
                    )
                    2 -> Step2NodeIdentity(
                        nodeName = nodeName,
                        onNodeNameChange = onNodeNameChange
                    )
                    3 -> Step3SecurityPin(
                        setupPin = setupPin,
                        isBiometricEnabled = isBiometricEnabled,
                        onPinChange = onPinChange,
                        onBiometricToggle = onBiometricToggle
                    )
                    4 -> Step4CloudVaultSelect(
                        selectedCloudType = selectedCloudType,
                        onCloudTypeSelect = onCloudTypeSelect
                    )
                    5 -> Step5VerificationLaunch(
                        nodeName = nodeName,
                        setupPin = setupPin
                    )
                }
            }

            // Bottom Navigation Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = onPreviousStepClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_setup_previous")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontSize = 13.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (currentStep < 5) {
                    Button(
                        onClick = onNextStepClick,
                        colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_setup_next")
                    ) {
                        Text("Continue", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = onCompleteSetupClick,
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_setup_complete")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LAUNCH QUANTUM VAULT", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1ArchitectureOverview(
    keyGenProgress: Float,
    isGeneratingKeys: Boolean,
    onGenerateKeysClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CardSlate)
                    .border(1.5.dp, QuantumCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = QuantumCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Post-Quantum Cryptography Setup",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Initialize local Kyber-1024 lattice keypair & phone hardware entropy pool. Zero central servers involved.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (keyGenProgress >= 1f) {
                Surface(
                    color = TacticalEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TacticalEmerald)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Kyber-1024 Keypair Generated & Stored in Local Hardware Keystore",
                            fontSize = 12.sp,
                            color = TacticalEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = onGenerateKeysClick,
                    enabled = !isGeneratingKeys,
                    colors = ButtonDefaults.buttonColors(containerColor = CardSlate, contentColor = QuantumCyan),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QuantumCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_generate_pqc_keys")
                ) {
                    Icon(Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGeneratingKeys) "Sampling SoC Entropy..." else "Sample Entropy & Generate PQC Keys",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isGeneratingKeys || keyGenProgress > 0f) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { keyGenProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = QuantumCyan,
                        trackColor = BorderSlate
                    )
                }
            }
        }
    }
}

@Composable
private fun Step2NodeIdentity(
    nodeName: String,
    onNodeNameChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Device Node Identity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose a display handle for your phone node in direct peer-to-peer channels.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nodeName,
                onValueChange = onNodeNameChange,
                label = { Text("Node Name / Handle", color = TextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_setup_node_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = QuantumCyan,
                    unfocusedBorderColor = BorderSlate,
                    focusedContainerColor = InnerBoxSlate,
                    unfocusedContainerColor = InnerBoxSlate
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Generated PQC Public Fingerprint:",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = InnerBoxSlate,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = QuantumCryptoEngine.devicePqcPublicKey,
                    fontSize = 10.sp,
                    color = TacticalEmerald,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun Step3SecurityPin(
    setupPin: String,
    isBiometricEnabled: Boolean,
    onPinChange: (String) -> Unit,
    onBiometricToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Vault Access PIN & Biometrics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enter a 6-digit security code and optionally enable biometric hardware (fingerprint/face) for fast vault access.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val isFilled = i < setupPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) QuantumCyan else InnerBoxSlate)
                            .border(1.dp, if (isFilled) QuantumCyan else BorderSlate, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = setupPin,
                onValueChange = onPinChange,
                label = { Text("Enter 6 Digits (e.g. 123456)", color = TextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_setup_pin"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = QuantumCyan,
                    unfocusedBorderColor = BorderSlate,
                    focusedContainerColor = InnerBoxSlate,
                    unfocusedContainerColor = InnerBoxSlate
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Biometric Option Switch Card
            Surface(
                color = InnerBoxSlate,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardSlate),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = TacticalEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Biometric Lock",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Use fingerprint / face biometrics to unlock",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { onBiometricToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = QuantumCyan,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSlate
                        ),
                        modifier = Modifier.testTag("setup_biometric_switch")
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4CloudVaultSelect(
    selectedCloudType: CloudProviderType,
    onCloudTypeSelect: (CloudProviderType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Select Primary Storage Vault",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "All backed-up messages & keys are encrypted client-side with AES-256-GCM before transfer.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val options = listOf(
                Pair(CloudProviderType.GOOGLE_DRIVE, "Google Drive (Zero-Knowledge Sync)"),
                Pair(CloudProviderType.DOWNLOADABLE_ZIP, "Local Encrypted Zip Archive"),
                Pair(CloudProviderType.WEBDAV, "WebDAV Secure Server")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for ((type, label) in options) {
                    val isSelected = type == selectedCloudType
                    Surface(
                        color = if (isSelected) QuantumCyan.copy(alpha = 0.15f) else InnerBoxSlate,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) QuantumCyan else BorderDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCloudTypeSelect(type) }
                            .testTag("setup_cloud_option_${type.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) QuantumCyan else TextPrimary
                            )
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = QuantumCyan)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step5VerificationLaunch(
    nodeName: String,
    setupPin: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TacticalEmerald))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = TacticalEmerald, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "System Ready & Configured",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            VerificationCheckItem("Post-Quantum Key Store (Kyber-1024)", "VERIFIED")
            VerificationCheckItem("Device Node Identity Handle", nodeName)
            VerificationCheckItem("Vault PIN Protection", "${setupPin.length} Digits Configured")
            VerificationCheckItem("Zero-Server Isolation Status", "100% LOCAL COMPUTE")

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = TacticalEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald)
            ) {
                Text(
                    text = "✓ Configuration complete! Tap below to open your quantum secure messenger.",
                    fontSize = 12.sp,
                    color = TacticalEmerald,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun VerificationCheckItem(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = QuantumCyan, fontFamily = FontFamily.Monospace)
    }
}
