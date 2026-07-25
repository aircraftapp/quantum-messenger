package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto.LocalComputeMetrics
import com.example.ui.ActiveCallState
import com.example.ui.theme.*

@Composable
fun PqcCallScreen(
    callState: ActiveCallState,
    computeMetrics: LocalComputeMetrics,
    onToggleMute: () -> Unit,
    onToggleCamera: () -> Unit,
    onEndCall: () -> Unit
) {
    var isPttWalkieMode by remember { mutableStateOf(false) }
    var isPttPressed by remember { mutableStateOf(false) }

    val durationFormatted = remember(callState.callDurationSeconds) {
        val mins = callState.callDurationSeconds / 60
        val secs = callState.callDurationSeconds % 60
        "%02d:%02d".format(mins, secs)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianBlack, DarkSlate, ObsidianBlack)
                )
            )
            .testTag("pqc_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info & Security Status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardSlate)
                        .border(1.dp, TacticalEmerald, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TacticalEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kyber-1024 Post-Quantum E2EE Stream",
                            fontSize = 12.sp,
                            color = TacticalEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Peer Avatar Placeholder / Video Canvas frame
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(DarkSlate)
                        .border(2.dp, QuantumCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (callState.isVideoCall) Icons.Default.Videocam else Icons.Default.Person,
                        contentDescription = null,
                        tint = QuantumCyan,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = callState.peerName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = durationFormatted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = QuantumCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Real-time Phone Local Compute Monitor Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = QuantumCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phone Hardware Encrypt & Encode",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = if (computeMetrics.hardwareAccelActive) "HW ACCEL ON" else "CPU",
                            fontSize = 10.sp,
                            color = TacticalEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ComputeStatPill("CPU Load", "${computeMetrics.cpuUsagePercentage}%")
                        ComputeStatPill("Encoding", "${computeMetrics.mediaEncodingFps} FPS")
                        ComputeStatPill("Crypto Ops", "${computeMetrics.cryptoOpsPerSecond}/s")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { computeMetrics.cpuUsagePercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = QuantumCyan,
                        trackColor = BorderSlate
                    )
                }
            }

            // Walkie-Talkie Half-Duplex Control
            if (isPttWalkieMode) {
                Surface(
                    color = WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, WarningAmber),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("call_walkie_talkie_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPttPressed) "🔴 TRANSMITTING LIVE AUDIO... OVER" else "📻 WALKIE-TALKIE MODE (HALF-DUPLEX)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = if (isPttPressed) AlertCrimson else WarningAmber,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clickable { isPttPressed = !isPttPressed }
                                .testTag("btn_call_ptt_talk")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isPttPressed) "RELEASE TO STOP (OVER & OUT)" else "PRESS TO TALK (PUSH-TO-TALK PTT)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ObsidianBlack,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Call Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Walkie Talkie Switch
                IconButton(
                    onClick = { isPttWalkieMode = !isPttWalkieMode },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isPttWalkieMode) WarningAmber else CardSlate)
                        .border(1.dp, BorderSlate, CircleShape)
                        .testTag("btn_call_walkie_talkie")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Walkie Talkie Mode",
                        tint = if (isPttWalkieMode) ObsidianBlack else WarningAmber
                    )
                }

                // Mute
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMuted) WarningAmber else CardSlate)
                        .border(1.dp, BorderSlate, CircleShape)
                        .testTag("btn_call_mute")
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (callState.isMuted) ObsidianBlack else TextPrimary
                    )
                }

                // End Call FAB
                FloatingActionButton(
                    onClick = onEndCall,
                    containerColor = AlertCrimson,
                    contentColor = TextPrimary,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("btn_call_end")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Camera Toggle
                IconButton(
                    onClick = onToggleCamera,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (callState.isCameraOff) WarningAmber else CardSlate)
                        .border(1.dp, BorderSlate, CircleShape)
                        .testTag("btn_call_camera")
                ) {
                    Icon(
                        imageVector = if (callState.isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Camera Toggle",
                        tint = if (callState.isCameraOff) ObsidianBlack else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ComputeStatPill(title: String, value: String) {
    Column {
        Text(text = title, fontSize = 10.sp, color = TextMuted)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = QuantumCyan,
            fontFamily = FontFamily.Monospace
        )
    }
}
