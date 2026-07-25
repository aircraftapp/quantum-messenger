package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatEntity
import com.example.data.local.ContactEntity
import com.example.data.local.StatusStoryEntity
import com.example.ui.Screen
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatListScreen(
    chats: List<ChatEntity>,
    contacts: List<ContactEntity> = emptyList(),
    chatDrafts: Map<String, String> = emptyMap(),
    statusStories: List<StatusStoryEntity> = emptyList(),
    onChatClick: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onPairNodeClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onLockVaultClick: () -> Unit,
    onReopenSetupClick: () -> Unit = {},
    onContactsClick: () -> Unit = {},
    onOpenQrScanner: () -> Unit = {},
    onOpenCreateStory: () -> Unit = {},
    onViewStory: (StatusStoryEntity) -> Unit = {},
    onOpenBroadcast: () -> Unit = {},
    onOpenCreateChannel: () -> Unit = {},
    onBulkArchive: (List<String>, Boolean) -> Unit = { _, _ -> },
    onBulkDelete: (List<String>) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Direct, 2: Groups, 3: Channels, 4: Archived
    var selectedChatIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val isSelectionMode = selectedChatIds.isNotEmpty()

    val filteredChats = chats.filter { chat ->
        val matchesTab = when (selectedFilterTab) {
            1 -> !chat.isGroup && !chat.isChannel && !chat.isArchived
            2 -> chat.isGroup && !chat.isChannel && !chat.isArchived
            3 -> chat.isChannel && !chat.isArchived
            4 -> chat.isArchived
            else -> !chat.isArchived // 0: All non-archived
        }
        val matchesSearch = searchQuery.isEmpty() ||
                chat.title.contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            text = "${selectedChatIds.size} Selected",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { selectedChatIds = emptySet() },
                            modifier = Modifier.testTag("btn_close_selection")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Selection", tint = TextPrimary)
                        }
                    },
                    actions = {
                        val allFilteredIds = filteredChats.map { it.id }.toSet()
                        val isAllSelected = allFilteredIds.isNotEmpty() && selectedChatIds.containsAll(allFilteredIds)
                        IconButton(
                            onClick = {
                                selectedChatIds = if (isAllSelected) emptySet() else selectedChatIds + allFilteredIds
                            },
                            modifier = Modifier.testTag("btn_select_all")
                        ) {
                            Icon(
                                imageVector = if (isAllSelected) Icons.Default.Deselect else Icons.Default.SelectAll,
                                contentDescription = if (isAllSelected) "Deselect All" else "Select All",
                                tint = QuantumCyan
                            )
                        }

                        val isViewingArchivedTab = selectedFilterTab == 4
                        IconButton(
                            onClick = {
                                onBulkArchive(selectedChatIds.toList(), !isViewingArchivedTab)
                                selectedChatIds = emptySet()
                            },
                            modifier = Modifier.testTag("btn_bulk_archive")
                        ) {
                            Icon(
                                imageVector = if (isViewingArchivedTab) Icons.Default.Unarchive else Icons.Default.Archive,
                                contentDescription = if (isViewingArchivedTab) "Unarchive Selected" else "Archive Selected",
                                tint = QuantumCyan
                            )
                        }

                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("btn_bulk_delete")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Selected",
                                tint = AlertCrimson
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSlate
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "QUANTUM MESSENGER",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TacticalEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PQC Kyber-1024 • Zero Server",
                                    fontSize = 11.sp,
                                    color = TacticalEmerald,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (filteredChats.isNotEmpty()) {
                                    selectedChatIds = setOf(filteredChats.first().id)
                                }
                            },
                            modifier = Modifier.testTag("btn_enter_selection_mode")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Select Conversations",
                                tint = QuantumCyan
                            )
                        }
                        IconButton(
                            onClick = onOpenQrScanner,
                            modifier = Modifier.testTag("btn_qr_scanner_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "PQC Key Exchange QR Scanner",
                                tint = QuantumCyan
                            )
                        }
                        IconButton(
                            onClick = onContactsClick,
                            modifier = Modifier.testTag("btn_contacts_directory")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contacts,
                                contentDescription = "Contacts Directory",
                                tint = QuantumCyan
                            )
                        }
                        IconButton(
                            onClick = { onReopenSetupClick() },
                            modifier = Modifier.testTag("btn_setup_config")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Setup & Configuration",
                                tint = QuantumCyan
                            )
                        }
                        IconButton(
                            onClick = { onNavigate(Screen.COMPUTE_DASHBOARD) },
                            modifier = Modifier.testTag("btn_compute_dashboard")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Phone Compute Monitor",
                                tint = QuantumCyan
                            )
                        }
                        IconButton(
                            onClick = { onLockVaultClick() },
                            modifier = Modifier.testTag("btn_lock_vault")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Vault",
                                tint = WarningAmber
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSlate
                    )
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = onOpenBroadcast,
                    containerColor = CardSlate,
                    contentColor = QuantumCyan,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("fab_broadcast")
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Send Broadcast")
                }

                SmallFloatingActionButton(
                    onClick = onOpenCreateChannel,
                    containerColor = CardSlate,
                    contentColor = TacticalEmerald,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("fab_create_channel")
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = "Create Channel")
                }

                FloatingActionButton(
                    onClick = onContactsClick,
                    containerColor = QuantumCyan,
                    contentColor = ObsidianBlack,
                    modifier = Modifier.testTag("fab_open_contacts")
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "Select Contact")
                }
            }
        },
        containerColor = ObsidianBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search encrypted node or message...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("input_search_chats"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardSlate,
                    unfocusedContainerColor = CardSlate,
                    focusedBorderColor = QuantumCyan,
                    unfocusedBorderColor = BorderSlate,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Disappearing Status / Stories Horizontal Carousel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DISAPPEARING STATUS & STORIES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(text = "24h Auto-Shred", fontSize = 10.sp, color = TacticalEmerald)
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add My Story Button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onOpenCreateStory() }
                                .testTag("btn_post_status_story")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(CardSlate)
                                    .border(1.5.dp, TacticalEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Add Story", tint = TacticalEmerald, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "My Status", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Contact Stories
                    items(statusStories, key = { it.id }) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onViewStory(story) }
                                .testTag("story_avatar_${story.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(QuantumCyan)
                                    .border(2.dp, TacticalEmerald, CircleShape)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.authorName.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = story.authorName,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Filter Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipItem("All", selectedFilterTab == 0) { selectedFilterTab = 0 }
                FilterChipItem("Direct", selectedFilterTab == 1) { selectedFilterTab = 1 }
                FilterChipItem("Groups", selectedFilterTab == 2) { selectedFilterTab = 2 }
                FilterChipItem("Channels", selectedFilterTab == 3) { selectedFilterTab = 3 }
                FilterChipItem("Archived", selectedFilterTab == 4) { selectedFilterTab = 4 }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredChats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedFilterTab == 4) Icons.Default.Archive else Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilterTab == 4) "No archived conversations" else "No encrypted channels found",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                val contactsMap = remember(contacts) { contacts.associateBy { it.id } }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredChats, key = { it.id }) { chat ->
                        val isSelected = selectedChatIds.contains(chat.id)
                        val draftText = chatDrafts[chat.id]
                        val contactEntity = contactsMap[chat.participantIdsCsv] ?: contacts.find { contact -> contact.name.equals(chat.title, ignoreCase = true) }
                        ChatItemRow(
                            chat = chat,
                            contactEntity = contactEntity,
                            draftText = draftText,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            onSelectToggle = {
                                selectedChatIds = if (isSelected) selectedChatIds - chat.id else selectedChatIds + chat.id
                            },
                            onClick = { onChatClick(chat.id) }
                        )
                    }
                }
            }
        }
    }

    // Bulk Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = AlertCrimson)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Conversations?", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete ${selectedChatIds.size} selected conversation(s) and all associated local messages?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBulkDelete(selectedChatIds.toList())
                        selectedChatIds = emptySet()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson, contentColor = Color.White),
                    modifier = Modifier.testTag("btn_confirm_bulk_delete")
                ) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSlate,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) QuantumCyan else CardSlate)
            .border(1.dp, if (isSelected) QuantumCyan else BorderSlate, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ObsidianBlack else TextSecondary
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ChatItemRow(
    chat: ChatEntity,
    contactEntity: ContactEntity? = null,
    draftText: String? = null,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onSelectToggle: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val timeFormatted = remember(chat.lastMessageTime) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(chat.lastMessageTime))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onSelectToggle() else onClick()
                },
                onLongClick = {
                    onSelectToggle()
                }
            )
            .testTag("chat_item_${chat.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) InnerBoxSlate else DarkSlate
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) QuantumCyan else BorderSlate)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelectToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = QuantumCyan,
                        uncheckedColor = TextMuted,
                        checkmarkColor = ObsidianBlack
                    ),
                    modifier = Modifier.testTag("checkbox_chat_${chat.id}")
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Node Avatar / Group / Channel Icon with Connection Status Badge Dot
            Box(
                modifier = Modifier.size(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardSlate)
                        .border(
                            1.dp,
                            when {
                                chat.isChannel -> TacticalEmerald
                                chat.isGroup -> WarningAmber
                                contactEntity?.isBlocked == true -> AlertCrimson
                                else -> QuantumCyan
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            chat.isChannel -> Icons.Default.Campaign
                            chat.isGroup -> Icons.Default.Groups
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = when {
                            chat.isChannel -> TacticalEmerald
                            chat.isGroup -> WarningAmber
                            contactEntity?.isBlocked == true -> AlertCrimson
                            else -> QuantumCyan
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Connection Status Indicator Dot (Online, Away, Offline, Blocked)
                if (!chat.isGroup && !chat.isChannel) {
                    val status = contactEntity?.presenceStatus ?: if (contactEntity?.isOnline == true) "ONLINE" else "OFFLINE"
                    val isBlocked = contactEntity?.isBlocked == true

                    val dotColor = when {
                        isBlocked -> AlertCrimson
                        status.equals("ONLINE", ignoreCase = true) -> TacticalEmerald
                        status.equals("AWAY", ignoreCase = true) -> WarningAmber
                        else -> TextMuted
                    }

                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(2.dp, DarkSlate, CircleShape)
                            .testTag("status_dot_${chat.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = chat.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (chat.isChannel) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = TacticalEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "📢 CHANNEL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalEmerald,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (contactEntity?.isBlocked == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AlertCrimson.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🚫 BLOCKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertCrimson,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = timeFormatted,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val effectiveDraft = if (!draftText.isNullOrBlank()) draftText else chat.draftText
                    if (effectiveDraft.isNotBlank()) {
                        Text(
                            text = "✍️ Draft: $effectiveDraft",
                            fontSize = 13.sp,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = chat.lastMessage,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (chat.ephemeralSettingSeconds > 0L) {
                        Spacer(modifier = Modifier.width(6.dp))
                        val timerText = when (chat.ephemeralSettingSeconds) {
                            5L -> "5s"
                            30L -> "30s"
                            300L -> "5m"
                            3600L -> "1h"
                            86400L -> "24h"
                            else -> "${chat.ephemeralSettingSeconds}s"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AlertCrimson.copy(alpha = 0.2f))
                                .border(0.5.dp, AlertCrimson, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⏱️ $timerText",
                                fontSize = 10.sp,
                                color = AlertCrimson,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
