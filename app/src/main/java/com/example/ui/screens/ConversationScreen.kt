package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.local.MessageEntity
import com.example.data.local.MessageType
import com.example.crypto.QuantumCryptoEngine
import com.example.ui.components.RealtimeAudioWaveformVisualizer
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chat: ChatEntity?,
    messages: List<MessageEntity>,
    messageInput: String,
    selectedEphemeralSeconds: Long,
    isRecordingAudio: Boolean,
    recordingDurationSeconds: Int,
    isWalkieTalkieModeActive: Boolean = false,
    selectedPttChannel: String = "CH-01 (446.006 MHz)",
    isPttTransmitting: Boolean = false,
    pttTransmissionDuration: Int = 0,
    latestPttReceivedAlert: String? = null,
    activeFileOperationName: String? = null,
    fileOperationProgress: Float = 0f,
    fileOperationStatus: String? = null,
    diagnosticMetrics: QuantumCryptoEngine.PqcDiagnosticMetrics = QuantumCryptoEngine.PqcDiagnosticMetrics(),
    isDiagnosticPanelOpen: Boolean = false,
    isChatSearchActive: Boolean = false,
    inChatSearchQuery: String = "",
    onBackClick: () -> Unit,
    onMessageInputChange: (String) -> Unit,
    onSendMessageClick: () -> Unit,
    onToggleAudioRecording: () -> Unit,
    onSendVideoNoteClick: () -> Unit,
    onSendFileClick: (String, String) -> Unit,
    onDecryptFileClick: (messageId: String, fileName: String) -> Unit = { _, _ -> },
    onEphemeralTimerClick: () -> Unit,
    onOpenSettingsClick: () -> Unit = {},
    onToggleReaction: (messageId: String, currentReactions: String, emoji: String) -> Unit = { _, _, _ -> },
    onStartCallClick: (Boolean) -> Unit,
    onToggleWalkieTalkieMode: () -> Unit = {},
    onSelectPttChannel: (String) -> Unit = {},
    onStartPttTransmission: () -> Unit = {},
    onStopPttTransmission: () -> Unit = {},
    onSendPttQuickBurst: (String) -> Unit = {},
    onDismissPttAlert: () -> Unit = {},
    onToggleDiagnosticPanel: () -> Unit = {},
    onToggleChatSearch: (Boolean) -> Unit = {},
    onUpdateInChatSearchQuery: (String) -> Unit = {},
    onExportChatHistoryClick: (passphrase: String) -> Unit = {},
    isContactBlocked: Boolean = false,
    onToggleBlockContact: (Boolean) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showPqcBadgeDialog by remember { mutableStateOf(false) }

    val bgThemeColor = remember(chat?.wallpaperTheme) {
        when (chat?.wallpaperTheme) {
            "MATRIX_GREEN" -> Color(0xFF021B0B)
            "CYBERPUNK" -> Color(0xFF0F172A)
            "MIDNIGHT_AURORA" -> Color(0xFF090D16)
            "SUNSET_GOLD" -> Color(0xFF1B1209)
            else -> ObsidianBlack
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val ephemeralLabel = remember(selectedEphemeralSeconds) {
        when (selectedEphemeralSeconds) {
            5L -> "5s"
            10L -> "10s"
            30L -> "30s"
            60L -> "1m"
            300L -> "5m"
            3600L -> "1h"
            86400L -> "24h"
            604800L -> "7d"
            else -> "Off"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clickable { onOpenSettingsClick() }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = chat?.title ?: "Encrypted Node",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
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
                                text = chat?.securityFingerprint ?: "PQC-8F92-VERIFIED",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_conversation")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Visual Post-Quantum Security Badge
                    Surface(
                        color = TacticalEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald),
                        modifier = Modifier
                            .clickable { showPqcBadgeDialog = true }
                            .padding(end = 4.dp)
                            .testTag("pqc_security_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EnhancedEncryption,
                                contentDescription = "Post-Quantum Kyber Lock",
                                tint = TacticalEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KYBER-1024",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalEmerald,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Ephemeral Timer Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedEphemeralSeconds > 0) AlertCrimson.copy(alpha = 0.2f) else CardSlate)
                            .border(1.dp, if (selectedEphemeralSeconds > 0) AlertCrimson else BorderSlate, RoundedCornerShape(12.dp))
                            .clickable { onEphemeralTimerClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_ephemeral_timer_selector")
                    ) {
                        Text(
                            text = "⏱️ $ephemeralLabel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEphemeralSeconds > 0) AlertCrimson else TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Walkie-Talkie PTT Mode Toggle Button
                    IconButton(
                        onClick = onToggleWalkieTalkieMode,
                        modifier = Modifier
                            .testTag("btn_walkie_talkie_toggle")
                            .background(
                                if (isWalkieTalkieModeActive) WarningAmber.copy(alpha = 0.25f) else Color.Transparent,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (isWalkieTalkieModeActive) WarningAmber else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Walkie Talkie Mode",
                            tint = if (isWalkieTalkieModeActive) WarningAmber else QuantumCyan
                        )
                    }

                    // In-Chat Message Search Toggle Button
                    IconButton(
                        onClick = { onToggleChatSearch(!isChatSearchActive) },
                        modifier = Modifier
                            .testTag("btn_toggle_chat_search")
                            .background(
                                if (isChatSearchActive) QuantumCyan.copy(alpha = 0.25f) else Color.Transparent,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (isChatSearchActive) QuantumCyan else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Message History",
                            tint = if (isChatSearchActive) QuantumCyan else TextPrimary
                        )
                    }

                    // Diagnostic Telemetry Panel Toggle Button
                    IconButton(
                        onClick = onToggleDiagnosticPanel,
                        modifier = Modifier
                            .testTag("btn_diagnostic_panel_toggle")
                            .background(
                                if (isDiagnosticPanelOpen) QuantumCyan.copy(alpha = 0.25f) else Color.Transparent,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (isDiagnosticPanelOpen) QuantumCyan else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Diagnostic Telemetry Panel",
                            tint = if (isDiagnosticPanelOpen) QuantumCyan else TextSecondary
                        )
                    }

                    IconButton(onClick = { onStartCallClick(false) }, modifier = Modifier.testTag("btn_audio_call")) {
                        Icon(Icons.Default.Call, contentDescription = "PQC Audio Call", tint = QuantumCyan)
                    }

                    IconButton(onClick = { onStartCallClick(true) }, modifier = Modifier.testTag("btn_video_call")) {
                        Icon(Icons.Default.Videocam, contentDescription = "PQC Video Call", tint = QuantumCyan)
                    }

                    IconButton(onClick = onOpenSettingsClick, modifier = Modifier.testTag("btn_open_chat_settings")) {
                        Icon(Icons.Default.Settings, contentDescription = "Chat Settings", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlate)
            )
        },
        containerColor = bgThemeColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Real-Time P2P & PQC Diagnostic Telemetry Panel
            if (isDiagnosticPanelOpen) {
                PqcDiagnosticPanel(
                    metrics = diagnosticMetrics,
                    onCloseClick = onToggleDiagnosticPanel
                )
            }

            // In-Chat Message Search Banner Overlay
            if (isChatSearchActive) {
                Surface(
                    color = DarkSlate,
                    border = BorderStroke(1.dp, QuantumCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("in_chat_search_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = QuantumCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = inChatSearchQuery,
                            onValueChange = onUpdateInChatSearchQuery,
                            placeholder = { Text("Search keywords in chat history...", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_in_chat_search_query"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSlate,
                                unfocusedContainerColor = CardSlate,
                                focusedBorderColor = QuantumCyan,
                                unfocusedBorderColor = BorderSlate,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { onToggleChatSearch(false) },
                            modifier = Modifier.size(32.dp).testTag("btn_close_chat_search")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search", tint = TextMuted)
                        }
                    }
                }
            }

            // Live Walkie-Talkie Received Transmission Banner Alert
            if (!latestPttReceivedAlert.isNullOrBlank()) {
                Surface(
                    color = WarningAmber.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, WarningAmber),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ptt_received_alert_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = latestPttReceivedAlert,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        IconButton(
                            onClick = onDismissPttAlert,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = WarningAmber
                            )
                        }
                    }
                }
            }

            // Tactical Walkie-Talkie Push-To-Talk (PTT) Mode Panel
            if (isWalkieTalkieModeActive) {
                Surface(
                    color = CardSlate,
                    border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("p2p_walkie_talkie_panel")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Channel Selector Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CellTower,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PTT TRANSCEIVER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "SQUELCH 100% • KYBER-1024",
                                fontSize = 10.sp,
                                color = TacticalEmerald,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Frequency Channels Chips
                        val channels = listOf("CH-01 (446.006 MHz)", "CH-02 (446.018 MHz)", "CH-03 (446.031 MHz)", "CH-04 (446.044 MHz)")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            channels.forEach { ch ->
                                val selected = selectedPttChannel == ch
                                Surface(
                                    color = if (selected) WarningAmber else DarkSlate,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (selected) WarningAmber else BorderSlate),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectPttChannel(ch) }
                                ) {
                                    Text(
                                        text = ch.take(5),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) ObsidianBlack else TextPrimary,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // REAL-TIME AUDIO WAVEFORM VISUALIZER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(InnerBoxSlate)
                                .border(0.5.dp, if (isPttTransmitting) AlertCrimson else WarningAmber, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            RealtimeAudioWaveformVisualizer(
                                isTransmitting = isPttTransmitting,
                                isReceiving = !latestPttReceivedAlert.isNullOrBlank(),
                                barCount = 30,
                                activeColor = if (isPttTransmitting) AlertCrimson else WarningAmber,
                                height = 30.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // MAIN PUSH-TO-TALK (PTT) TRANSMISSION BUTTON
                        Surface(
                            color = if (isPttTransmitting) AlertCrimson else WarningAmber,
                            shape = RoundedCornerShape(28.dp),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    if (isPttTransmitting) {
                                        onStopPttTransmission()
                                    } else {
                                        onStartPttTransmission()
                                    }
                                }
                                .testTag("btn_push_to_talk_ptt")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPttTransmitting) Icons.Default.Mic else Icons.Default.GraphicEq,
                                    contentDescription = "Push To Talk",
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isPttTransmitting) "🔴 TRANSMITTING LIVE VOICE (${pttTransmissionDuration}s)... TAP TO SEND" else "🎙️ PRESS TO TALK (PUSH-TO-TALK PTT)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ObsidianBlack,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Tactical Voice Burst Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickBursts = listOf("ROGER THAT", "AFFIRMATIVE", "STATUS CHECK", "OVER & OUT")
                            quickBursts.forEach { burst ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(InnerBoxSlate)
                                        .border(0.5.dp, BorderSlate, RoundedCornerShape(6.dp))
                                        .clickable { onSendPttQuickBurst("📻 WALKIE-TALKIE [$burst] • $selectedPttChannel - OVER & OUT") }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = burst,
                                        fontSize = 9.sp,
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

            // Active Phone Hardware Encrypt / Decrypt Operation Banner
            if (!activeFileOperationName.isNullOrBlank()) {
                Surface(
                    color = CardSlate,
                    border = BorderStroke(1.dp, QuantumCyan),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth().testTag("file_operation_banner")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = QuantumCyan,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = activeFileOperationName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = QuantumCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${(fileOperationProgress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalEmerald,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (!fileOperationStatus.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fileOperationStatus,
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { fileOperationProgress },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = QuantumCyan,
                            trackColor = InnerBoxSlate
                        )
                    }
                }
            }
            val displayedMessages = remember(messages, inChatSearchQuery) {
                if (inChatSearchQuery.isBlank()) {
                    messages
                } else {
                    messages.filter { it.textContent.contains(inChatSearchQuery, ignoreCase = true) }
                }
            }

            if (inChatSearchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(QuantumCyan.copy(alpha = 0.15f))
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔍 ${displayedMessages.size} matching message(s) for '$inChatSearchQuery'",
                        fontSize = 11.sp,
                        color = QuantumCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSlate)
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒 Zero-Knowledge Channel • Kyber-1024 PQC • No Central Server",
                    fontSize = 11.sp,
                    color = TacticalEmerald,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Message History List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedMessages, key = { it.id }) { message ->
                    MessageBubbleRow(
                        message = message,
                        onToggleReaction = onToggleReaction,
                        onDecryptFileClick = onDecryptFileClick
                    )
                }
            }

            // Attachment Menu Popup
            if (showAttachmentMenu) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttachmentOptionItem(
                            icon = Icons.Default.Videocam,
                            label = "Video Note",
                            color = QuantumCyan
                        ) {
                            showAttachmentMenu = false
                            onSendVideoNoteClick()
                        }

                        AttachmentOptionItem(
                            icon = Icons.Default.InsertDriveFile,
                            label = "Encrypted Document",
                            color = TacticalEmerald
                        ) {
                            showAttachmentMenu = false
                            onSendFileClick("Security_Brief_2026.pdf", "4.8 MB")
                        }

                        AttachmentOptionItem(
                            icon = Icons.Default.Image,
                            label = "Media Transfer",
                            color = WarningAmber
                        ) {
                            showAttachmentMenu = false
                            onSendFileClick("Cipher_Intel_Map.png", "12.2 MB")
                        }
                    }
                }
            }

            // Blocked User Banner Alert
            if (isContactBlocked) {
                Surface(
                    color = AlertCrimson.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, AlertCrimson),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blocked_user_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = AlertCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🚫 CONTACT BLOCKED • P2P SESSION SUSPENDED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertCrimson,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { onToggleBlockContact(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_unblock_user_in_chat")
                        ) {
                            Text("Unblock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = DarkSlate,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showAttachmentMenu = !showAttachmentMenu },
                        modifier = Modifier.testTag("btn_attachment_menu")
                    ) {
                        Icon(
                            imageVector = if (showAttachmentMenu) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Attach",
                            tint = QuantumCyan
                        )
                    }

                    if (isRecordingAudio) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(AlertCrimson.copy(alpha = 0.2f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AlertCrimson)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🎤 Local Hardware Encoded Audio 0:${if (recordingDurationSeconds < 10) "0" else ""}$recordingDurationSeconds",
                                fontSize = 13.sp,
                                color = AlertCrimson,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = onMessageInputChange,
                            placeholder = { Text("Encrypted message...", color = TextMuted, fontSize = 14.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_message_text"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSlate,
                                unfocusedContainerColor = CardSlate,
                                focusedBorderColor = QuantumCyan,
                                unfocusedBorderColor = BorderSlate,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Voice Recorder Button
                    IconButton(
                        onClick = onToggleAudioRecording,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isRecordingAudio) AlertCrimson else CardSlate)
                            .testTag("btn_record_voice")
                    ) {
                        Icon(
                            imageVector = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record Voice Note",
                            tint = if (isRecordingAudio) ObsidianBlack else QuantumCyan
                        )
                    }

                    if (messageInput.isNotBlank() && !isRecordingAudio) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onSendMessageClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(QuantumCyan)
                                .testTag("btn_send_message")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = ObsidianBlack
                            )
                        }
                    }
                }
            }
        }

        if (showPqcBadgeDialog) {
            AlertDialog(
                onDismissRequest = { showPqcBadgeDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EnhancedEncryption,
                            contentDescription = null,
                            tint = TacticalEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POST-QUANTUM SECURITY",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = InnerBoxSlate,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ENCRYPTION STATUS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(TacticalEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "VERIFIED PQC",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TacticalEmerald
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "CRYSTALS-Kyber-1024 (NIST FIPS 203)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalEmerald
                                )
                                Text(
                                    text = "Key Encapsulation Mechanism (KEM)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Symmetric Cipher:", fontSize = 11.sp, color = TextMuted)
                                Text(text = "AES-256-GCM / 128-bit IV", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Peer Fingerprint:", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = chat?.securityFingerprint ?: "PQC-8F92-VERIFIED",
                                    fontSize = 11.sp,
                                    color = QuantumCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Zero-Knowledge Transport:", fontSize = 11.sp, color = TextMuted)
                                Text(text = "Direct WebSocket P2P", fontSize = 11.sp, color = TacticalEmerald)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Quantum Resistant:", fontSize = 11.sp, color = TextMuted)
                                Text(text = "100% Shielded (Post-Quantum Ready)", fontSize = 11.sp, color = TacticalEmerald)
                            }
                        }

                        Surface(
                            color = ObsidianBlack,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "All messages and media transferred over this P2P channel are sealed using Kyber-1024 post-quantum key encapsulation and AES-256-GCM symmetric ciphers. No unencrypted data touches central servers or disk.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPqcBadgeDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald, contentColor = ObsidianBlack),
                        modifier = Modifier.testTag("btn_close_pqc_dialog")
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSlate,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MessageBubbleRow(
    message: MessageEntity,
    onToggleReaction: (messageId: String, currentReactions: String, emoji: String) -> Unit = { _, _, _ -> },
    onDecryptFileClick: (messageId: String, fileName: String) -> Unit = { _, _ -> }
) {
    val isMe = message.isFromMe
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) BubbleSent else BubbleReceived

    var showReactionPicker by remember { mutableStateOf(false) }
    var isVoicePlaying by remember { mutableStateOf(false) }
    var voiceProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isVoicePlaying) {
        if (isVoicePlaying) {
            voiceProgress = 0f
            while (voiceProgress < 1f && isVoicePlaying) {
                kotlinx.coroutines.delay(100)
                voiceProgress += 0.05f
            }
            isVoicePlaying = false
        }
    }

    val availableEmojis = listOf("👍", "❤️", "🔥", "😮", "🙏", "🚀", "🛡️")

    var remainingSeconds by remember(message.id, message.expiresAtTimestamp) {
        mutableLongStateOf(
            if (message.expiresAtTimestamp > 0L) {
                ((message.expiresAtTimestamp - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
            } else 0L
        )
    }

    LaunchedEffect(message.expiresAtTimestamp) {
        if (message.expiresAtTimestamp > 0L) {
            while (remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                remainingSeconds = ((message.expiresAtTimestamp - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
            }
        }
    }

    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        // Reaction Picker Popup Overlay
        if (showReactionPicker) {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuantumCyan)),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .testTag("reaction_picker_${message.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableEmojis.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    showReactionPicker = false
                                    onToggleReaction(message.id, message.reactionEmojis, emoji)
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .testTag("message_bubble_${message.id}"),
            contentAlignment = alignment
        ) {
            Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                // Ephemeral Shredder Countdown Bar
                if (message.ephemeralDurationSeconds > 0L && remainingSeconds > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AlertCrimson.copy(alpha = 0.25f))
                            .border(0.5.dp, AlertCrimson, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AlertCrimson,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shredding in ${remainingSeconds}s",
                            fontSize = 11.sp,
                            color = AlertCrimson,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            )
                        )
                        .background(bubbleColor)
                        .border(
                            1.dp,
                            if (isMe) QuantumCyan.copy(alpha = 0.4f) else BorderSlate,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { showReactionPicker = !showReactionPicker }
                        .padding(12.dp)
                ) {
                    Column {
                        if (!isMe) {
                            Text(
                                text = message.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalEmerald
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        when (message.messageType) {
                            MessageType.TEXT -> {
                                Text(
                                    text = message.textContent,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            MessageType.VOICE -> {
                                // ANIMATED WAVEFORM VOICE NOTE PLAYER
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { isVoicePlaying = !isVoicePlaying },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(QuantumCyan)
                                                .testTag("btn_play_voice_${message.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play Voice Note",
                                                tint = ObsidianBlack,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            // Waveform bars
                                            Row(
                                                modifier = Modifier.fillMaxWidth().height(24.dp),
                                                verticalAlignment = Alignment.Bottom,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                val barHeights = listOf(12, 18, 8, 22, 14, 24, 10, 16, 20, 12, 24, 8, 18, 14)
                                                barHeights.forEachIndexed { idx, h ->
                                                    val active = (idx.toFloat() / barHeights.size) <= voiceProgress
                                                    val animatedH = if (isVoicePlaying) (h * (0.6f + 0.4f * Math.sin((idx + voiceProgress * 20).toDouble()))).dp else h.dp
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(animatedH)
                                                            .clip(RoundedCornerShape(2.dp))
                                                            .background(if (active) TacticalEmerald else QuantumCyan.copy(alpha = 0.4f))
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "${(voiceProgress * (message.mediaDurationSeconds.takeIf { it > 0 } ?: 15)).toInt()}s / ${message.mediaDurationSeconds.takeIf { it > 0 } ?: 15}s • Voice",
                                                fontSize = 10.sp,
                                                color = TextSecondary,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                            MessageType.VIDEO -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = TacticalEmerald,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "📹 PQC Encrypted Video Note",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "12s • Hardware Encoded",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                            MessageType.FILE -> {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = WarningAmber,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = message.textContent,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${message.mediaSizeFormatted ?: "1.2 MB"} • Kyber-1024 Zero-Knowledge E2EE",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Button(
                                        onClick = { onDecryptFileClick(message.id, message.textContent) },
                                        colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(32.dp).testTag("btn_decrypt_file_${message.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Decrypt & Save on Phone CPU", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            MessageType.WALKIE_TALKIE -> {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(
                                            onClick = { isVoicePlaying = !isVoicePlaying },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(WarningAmber)
                                                .testTag("btn_play_ptt_${message.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                                                contentDescription = "Play PTT Audio",
                                                tint = ObsidianBlack,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = message.textContent,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WarningAmber,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "SQUELCH OK • ${message.mediaDurationSeconds.coerceAtLeast(3)}s TRANSMISSION",
                                                fontSize = 10.sp,
                                                color = TacticalEmerald,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth().height(16.dp),
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        val pttBars = listOf(10, 16, 8, 14, 18, 12, 16, 6, 14, 12, 18, 10, 14, 8)
                                        pttBars.forEachIndexed { idx, h ->
                                            val active = (idx.toFloat() / pttBars.size) <= voiceProgress
                                            val animatedH = if (isVoicePlaying) (h * (0.5f + 0.5f * Math.sin((idx + voiceProgress * 15).toDouble()))).dp else h.dp
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(animatedH)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(if (active) WarningAmber else BorderSlate)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Kyber-1024",
                                fontSize = 9.sp,
                                color = QuantumCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = timeFormatted,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Attached Reaction Badge Bar
                if (message.reactionEmojis.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .testTag("reaction_badges_${message.id}")
                    ) {
                        message.reactionEmojis.split(",").filter { it.isNotBlank() }.forEach { em ->
                            Surface(
                                color = DarkSlate,
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSlate))
                            ) {
                                Text(
                                    text = em,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PqcDiagnosticPanel(
    metrics: QuantumCryptoEngine.PqcDiagnosticMetrics,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardSlate,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, QuantumCyan),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("pqc_diagnostic_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TacticalEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REAL-TIME DIAGNOSTIC TELEMETRY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = QuantumCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Diagnostic Panel",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Card Sections (P2P Latency, Walkie Signal, PQC Session)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Card 1: P2P Latency & Connection
                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = TacticalEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "P2P NETWORK LATENCY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "RTT: ${metrics.p2pLatencyMs} ms",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TacticalEmerald,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Loss: ${metrics.p2pPacketLossPercent}%", fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            Text(text = "Bandwidth: ${metrics.p2pBandwidthKbps} Kbps", fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            Text(text = "Direct Mesh WSS", fontSize = 10.sp, color = QuantumCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Card 2: Walkie-Talkie Signal Quality
                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WALKIE-TALKIE SIGNAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${metrics.walkieTalkieSignalDbm} dBm (EXCELLENT)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Squelch: ${metrics.walkieTalkieSquelchPercent}%", fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            Text(text = "SNR: ${metrics.walkieTalkieSnrDb} dB", fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            Text(text = metrics.walkieTalkieAudioQuality, fontSize = 10.sp, color = WarningAmber, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Card 3: Post-Quantum Cryptographic Session Details
                Surface(
                    color = InnerBoxSlate,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = QuantumCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PQC SESSION DETAILS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "Re-Key: ${metrics.pqcKeyRekeyCountdownSec}s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuantumCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "KEM:", fontSize = 10.sp, color = TextMuted)
                                Text(text = metrics.pqcKemAlgorithm, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Signature:", fontSize = 10.sp, color = TextMuted)
                                Text(text = metrics.pqcDigitalSignature, fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Symmetric Cipher:", fontSize = 10.sp, color = TextMuted)
                                Text(text = metrics.pqcSymmetricCipher, fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Quantum Entropy:", fontSize = 10.sp, color = TextMuted)
                                Text(text = "${metrics.pqcQuantumEntropyScore}% (Hardware TRNG)", fontSize = 10.sp, color = QuantumCyan, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Session Token:", fontSize = 10.sp, color = TextMuted)
                                Text(text = "${metrics.pqcSessionId} (Zero-Knowledge)", fontSize = 10.sp, color = WarningAmber, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
