package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricAuthHelper
import com.example.security.BiometricStatus
import com.example.ui.theme.*

@Composable
fun BiometricLockScreen(
    pinInput: String,
    pinError: Boolean,
    isBiometricEnabled: Boolean = true,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBiometricAuthSuccess: () -> Unit,
    onToggleBiometricOption: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var biometricErrorMessage by remember { mutableStateOf<String?>(null) }

    fun triggerBiometricPrompt() {
        if (!isBiometricEnabled) {
            biometricErrorMessage = "Biometric authentication is disabled in security settings."
            return
        }

        if (activity != null) {
            val status = BiometricAuthHelper.checkBiometricAvailability(activity)
            if (status == BiometricStatus.AVAILABLE) {
                BiometricAuthHelper.launchBiometricPrompt(
                    activity = activity,
                    title = "Quantum Vault Security",
                    subtitle = "Verify biometrics (fingerprint/face) to access encrypted chats",
                    negativeButtonText = "Use Security PIN",
                    onSuccess = {
                        biometricErrorMessage = null
                        onBiometricAuthSuccess()
                    },
                    onError = { err ->
                        biometricErrorMessage = err
                    }
                )
            } else if (status == BiometricStatus.NOT_ENROLLED) {
                biometricErrorMessage = "No biometrics enrolled on device. Please enter PIN."
            } else if (status == BiometricStatus.NO_HARDWARE) {
                biometricErrorMessage = "Biometric hardware not available on device."
            } else {
                biometricErrorMessage = "Biometrics currently unavailable. Use PIN."
            }
        } else {
            // Simulated success if not attached to FragmentActivity (e.g. preview)
            onBiometricAuthSuccess()
        }
    }

    LaunchedEffect(Unit) {
        if (isBiometricEnabled) {
            triggerBiometricPrompt()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianBlack, DarkSlate, ObsidianBlack)
                )
            )
            .padding(24.dp)
            .testTag("biometric_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Security Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CardSlate)
                    .border(1.5.dp, QuantumCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Zero Knowledge Vault",
                    tint = QuantumCyan,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "QUANTUM MESSENGER",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSlate)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TacticalEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kyber-1024 Zero-Knowledge Vault Locked",
                    fontSize = 12.sp,
                    color = TacticalEmerald,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val isFilled = i < pinInput.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) (if (pinError) AlertCrimson else QuantumCyan)
                                else CardSlate
                            )
                            .border(
                                width = 1.dp,
                                color = if (pinError) AlertCrimson else BorderSlate,
                                shape = CircleShape
                            )
                    )
                }
            }

            if (pinError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Incorrect Security PIN. Access Denied.",
                    color = AlertCrimson,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (biometricErrorMessage != null && !pinError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = biometricErrorMessage!!,
                    color = WarningAmber,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tactical Keypad (1-9, Biometric, 0, Backspace)
            val digits = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in digits) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        for (digit in row) {
                            KeypadButton(
                                text = digit,
                                onClick = { onDigitClick(digit) }
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Biometric Trigger Button
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (isBiometricEnabled) CardSlate else InnerBoxSlate)
                            .border(1.dp, if (isBiometricEnabled) TacticalEmerald else BorderDark, CircleShape)
                            .clickable { triggerBiometricPrompt() }
                            .testTag("biometric_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Unlock Prompt",
                            tint = if (isBiometricEnabled) TacticalEmerald else TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    KeypadButton(
                        text = "0",
                        onClick = { onDigitClick("0") }
                    )

                    // Backspace
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CardSlate)
                            .border(1.dp, BorderSlate, CircleShape)
                            .clickable { onBackspaceClick() }
                            .testTag("pin_backspace_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap fingerprint sensor or enter PIN",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            if (onToggleBiometricOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSlate)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = QuantumCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Biometrics Option",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { onToggleBiometricOption(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBlack,
                            checkedTrackColor = QuantumCyan,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = InnerBoxSlate
                        ),
                        modifier = Modifier
                            .scale(0.8f)
                            .testTag("switch_biometric_lock")
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(CardSlate)
            .border(1.dp, BorderSlate, CircleShape)
            .clickable { onClick() }
            .testTag("keypad_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
