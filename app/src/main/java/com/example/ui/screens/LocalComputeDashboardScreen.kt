package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto.LocalComputeMetrics
import com.example.crypto.QuantumCryptoEngine
import com.example.network.p2p.P2pPeerNode
import com.example.network.p2p.P2pServerStatus
import com.example.ui.MediaJobUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalComputeDashboardScreen(
    computeMetrics: LocalComputeMetrics,
    activeMediaJobs: List<MediaJobUiState> = emptyList(),
    p2pServerStatus: P2pServerStatus = P2pServerStatus.LISTENING,
    p2pServerInfo: String = "Listening on ws://127.0.0.1:8888",
    p2pActivePeers: List<P2pPeerNode> = emptyList(),
    dataUsageMetrics: QuantumCryptoEngine.P2pDataUsageMetrics = QuantumCryptoEngine.P2pDataUsageMetrics(),
    globalEphemeralTtl: Long = 30L,
    onToggleP2pServer: (Boolean) -> Unit = {},
    onConnectToP2pPeer: (String) -> Unit = {},
    onEnqueueTestJob: (mediaType: String) -> Unit = {},
    onToggleBatterySaver: (Boolean) -> Unit = {},
    onResetDataUsage: () -> Unit = {},
    onSetGlobalEphemeralTtl: (Long) -> Unit = {},
    onBackClick: () -> Unit
) {
    var peerConnectInput by remember { mutableStateOf("") }
    var connectStatusMessage by remember { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phone Compute Resource Monitor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_compute_dashboard")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlate)
            )
        },
        containerColor = ObsidianBlack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Main Hardware Acceleration Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TacticalEmerald))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(TacticalEmerald.copy(alpha = 0.2f))
                                .border(1.5.dp, TacticalEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = TacticalEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Zero Server • 100% Phone Compute",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Audio, video, & media encoding executed locally via phone SoC hardware.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            item {
                // Metric Grid Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "CPU Utilization",
                        value = "${computeMetrics.cpuUsagePercentage}%",
                        subtitle = "Local SoC Core Load",
                        icon = Icons.Default.Memory,
                        accentColor = QuantumCyan,
                        progress = computeMetrics.cpuUsagePercentage / 100f,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "RAM Allocation",
                        value = "${computeMetrics.ramAllocatedMb} MB",
                        subtitle = "PQC Key Store Memory",
                        icon = Icons.Default.SdCard,
                        accentColor = WarningAmber,
                        progress = (computeMetrics.ramAllocatedMb / 512f).coerceIn(0f, 1f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Crypto Throughput",
                        value = "${computeMetrics.cryptoOpsPerSecond}",
                        subtitle = "Kyber Ops / Second",
                        icon = Icons.Default.VpnKey,
                        accentColor = TacticalEmerald,
                        progress = (computeMetrics.cryptoOpsPerSecond / 20000f).coerceIn(0f, 1f),
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Media Encoding",
                        value = "${computeMetrics.mediaEncodingFps} FPS",
                        subtitle = "Hardware AV1 / H.265",
                        icon = Icons.Default.Videocam,
                        accentColor = QuantumCyan,
                        progress = computeMetrics.mediaEncodingFps / 60f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                // Entropy Pool & Key Rotation Logs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "POST-QUANTUM ENTROPY & KEY ROTATIONS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuantumCyan,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Entropy Pool Strength", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                text = "${computeMetrics.pqEntropyPoolBitStrength}-bit (NIST Level 5)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalEmerald,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Active Session Key Rotations", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                text = "${computeMetrics.activeKeyRotationsCount} Completed",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            item {
                // P2P WEBSOCKET NETWORKING LAYER CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TacticalEmerald))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = null,
                                    tint = TacticalEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "DIRECT P2P WEBSOCKET NETWORK",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalEmerald,
                                        letterSpacing = 1.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Serverless device-to-device encrypted socket mesh",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Switch(
                                checked = p2pServerStatus == P2pServerStatus.LISTENING,
                                onCheckedChange = { onToggleP2pServer(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ObsidianBlack,
                                    checkedTrackColor = TacticalEmerald,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = InnerBoxSlate
                                ),
                                modifier = Modifier.testTag("switch_toggle_p2p_server")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = InnerBoxSlate,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Server Socket Endpoint", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = p2pServerInfo,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = QuantumCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (p2pServerStatus == P2pServerStatus.LISTENING) TacticalEmerald.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = p2pServerStatus.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (p2pServerStatus == P2pServerStatus.LISTENING) TacticalEmerald else WarningAmber
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "CONNECT DIRECT TO PEER IP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = peerConnectInput,
                                onValueChange = { peerConnectInput = it },
                                placeholder = { Text("192.168.1.150:8888", fontSize = 12.sp, color = TextMuted) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = TacticalEmerald,
                                    unfocusedBorderColor = BorderSlate
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_connect_peer_address")
                            )

                            Button(
                                onClick = {
                                    if (peerConnectInput.isNotBlank()) {
                                        connectStatusMessage = "Connecting to ws://$peerConnectInput..."
                                        onConnectToP2pPeer(peerConnectInput)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                                modifier = Modifier.testTag("btn_connect_p2p_peer")
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (connectStatusMessage != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = connectStatusMessage!!, fontSize = 11.sp, color = QuantumCyan)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Peers List
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE CONNECTED P2P PEERS (${p2pActivePeers.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (p2pActivePeers.isEmpty()) {
                            Text(
                                text = "No active P2P WebSocket connections. Connect to a remote peer address above or wait for incoming peer socket handshake.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            p2pActivePeers.forEach { peer ->
                                Surface(
                                    color = InnerBoxSlate,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(TacticalEmerald)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(text = peer.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Text(text = peer.address, fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                            }
                                        }

                                        Text(
                                            text = "DIRECT WS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = QuantumCyan,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // WorkManager Local Media Transcoding & Encryption Queue Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuantumCyan))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "WORKMANAGER BACKGROUND MEDIA PIPELINE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = QuantumCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Text(
                            text = "Zero-knowledge on-device image EXIF stripping, hardware transcoding & AES-256-GCM encryption.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onEnqueueTestJob("IMAGE") },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate, contentColor = QuantumCyan),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_enqueue_transcode_image"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Process Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onEnqueueTestJob("VIDEO") },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate, contentColor = TacticalEmerald),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_enqueue_transcode_video"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Transcode Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (activeMediaJobs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "ACTIVE BACKGROUND WORKERS", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            activeMediaJobs.takeLast(4).forEach { job ->
                                Surface(
                                    color = InnerBoxSlate,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "[${job.id}] ${job.mediaType}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(text = "${job.progress}%", fontSize = 11.sp, color = TacticalEmerald, fontFamily = FontFamily.Monospace)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { job.progress / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = if (job.isCompleted) TacticalEmerald else QuantumCyan,
                                            trackColor = BorderSlate
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = job.statusText, fontSize = 10.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- FEATURE 1: P2P BANDWIDTH CONSUMPTION DASHBOARD ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("p2p_data_usage_dashboard_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuantumCyan))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DataUsage,
                                    contentDescription = null,
                                    tint = QuantumCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "P2P NETWORK BANDWIDTH DASHBOARD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = QuantumCyan,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = onResetDataUsage,
                                colors = ButtonDefaults.buttonColors(containerColor = InnerBoxSlate, contentColor = TextSecondary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("btn_reset_data_usage")
                            ) {
                                Text("Reset Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Usage Meter Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(text = "Total Bandwidth Consumed", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "${dataUsageMetrics.totalMbConsumed} MB",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TacticalEmerald,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "Cap: ${dataUsageMetrics.dailyBandwidthLimitMb.toInt()} MB/day",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val usageProgress = (dataUsageMetrics.totalMbConsumed / dataUsageMetrics.dailyBandwidthLimitMb).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { usageProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TacticalEmerald,
                            trackColor = BorderSlate
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category Breakdown Grid
                        Text(text = "TRAFFIC CATEGORY BREAKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = QuantumCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "P2P State Synchronization", fontSize = 11.sp, color = TextPrimary)
                                }
                                Text(text = "${dataUsageMetrics.stateSyncMb} MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumCyan, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Encrypted File Transfers", fontSize = 11.sp, color = TextPrimary)
                                }
                                Text(text = "${dataUsageMetrics.fileTransferMb} MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = TacticalEmerald, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Walkie-Talkie Audio Streams", fontSize = 11.sp, color = TextPrimary)
                                }
                                Text(text = "${dataUsageMetrics.walkieTalkieAudioMb} MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Off-Grid Cloud & Backups", fontSize = 11.sp, color = TextPrimary)
                                }
                                Text(text = "${dataUsageMetrics.cloudBackupMb} MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // --- FEATURE 2: BATTERY-SAVER MODE FOR P2P BACKGROUND SYNC ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("p2p_battery_saver_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (dataUsageMetrics.isBatterySaverEnabled) WarningAmber else BorderSlate
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.BatterySaver,
                                    contentDescription = null,
                                    tint = if (dataUsageMetrics.isBatterySaverEnabled) WarningAmber else TacticalEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "LOW-POWER P2P BACKGROUND SYNC",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 1.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (dataUsageMetrics.isBatterySaverEnabled) "Interval: 60s (Low Power)" else "Interval: 5s (High Performance)",
                                        fontSize = 10.sp,
                                        color = if (dataUsageMetrics.isBatterySaverEnabled) WarningAmber else TacticalEmerald,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Switch(
                                checked = dataUsageMetrics.isBatterySaverEnabled,
                                onCheckedChange = onToggleBatterySaver,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = WarningAmber,
                                    checkedTrackColor = WarningAmber.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TacticalEmerald,
                                    uncheckedTrackColor = InnerBoxSlate
                                ),
                                modifier = Modifier.testTag("btn_toggle_battery_saver")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = InnerBoxSlate,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSlate)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (dataUsageMetrics.isBatterySaverEnabled) Icons.Default.BatteryChargingFull else Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (dataUsageMetrics.isBatterySaverEnabled) WarningAmber else TacticalEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (dataUsageMetrics.isBatterySaverEnabled)
                                        "🔋 Battery Saver Active: Reduced background peer discovery pings and heartbeat check-ins to 60-second intervals to preserve CPU battery life during low-power state."
                                    else
                                        "⚡ High Performance Mesh: Active 5-second peer heartbeat pings for instantaneous off-grid connections and real-time walkie-talkie audio response.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- FEATURE 3: CONFIGURATION SETTING FOR SELF-DESTRUCTING MESSAGES (TTL) ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_ephemeral_ttl_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AlertCrimson))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = AlertCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GLOBAL SELF-DESTRUCTING MESSAGE TTL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertCrimson,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Define default Time-To-Live (TTL) for messages to ensure automated ephemeral compliance across all new conversations.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val ttlOptions = listOf(
                            0L to "Off (Persistent)",
                            10L to "10 Seconds",
                            30L to "30 Seconds",
                            300L to "5 Minutes",
                            3600L to "1 Hour",
                            86400L to "24 Hours"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ttlOptions.forEach { (seconds, label) ->
                                val isSelected = globalEphemeralTtl == seconds
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) AlertCrimson.copy(alpha = 0.2f) else InnerBoxSlate)
                                        .border(1.dp, if (isSelected) AlertCrimson else BorderSlate, RoundedCornerShape(8.dp))
                                        .clickable { onSetGlobalEphemeralTtl(seconds) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("btn_global_ttl_$seconds"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "DEFAULT RULE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AlertCrimson, fontFamily = FontFamily.Monospace)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = AlertCrimson, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSlate),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                Text(text = title, fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = BorderSlate
            )
        }
    }
}
