package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ContactEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsDirectoryScreen(
    contacts: List<ContactEntity>,
    searchQueryParam: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onBackClick: () -> Unit,
    onContactSelect: (ContactEntity) -> Unit,
    onAddContactSubmit: (name: String, phoneNumber: String, publicKey: String) -> Unit,
    onSyncPhonebookClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onCallContactClick: (ContactEntity, Boolean) -> Unit
) {
    var searchQuery by remember(searchQueryParam) { mutableStateOf(searchQueryParam) }
    var showAddContactModal by remember { mutableStateOf(false) }

    val filteredContacts = contacts.filter { contact ->
        if (searchQuery.isBlank()) true else {
            contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.phoneNumber.contains(searchQuery, ignoreCase = true) ||
                    contact.id.contains(searchQuery, ignoreCase = true) ||
                    (searchQuery.equals("online", ignoreCase = true) && contact.isOnline) ||
                    (searchQuery.equals("offline", ignoreCase = true) && !contact.isOnline)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Contacts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${contacts.size} PQC Verified Contacts",
                            fontSize = 11.sp,
                            color = QuantumCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("btn_contacts_back")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddContactModal = true },
                        modifier = Modifier.testTag("btn_add_contact_top")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Contact",
                            tint = QuantumCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSlate
                )
            )
        },
        containerColor = ObsidianBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("contacts_directory_screen")
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                placeholder = { Text("Search by name, phone, or status (online/offline)...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("input_search_contacts"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InnerBoxSlate,
                    unfocusedContainerColor = InnerBoxSlate,
                    focusedBorderColor = QuantumCyan,
                    unfocusedBorderColor = BorderSlate,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            // Top Action Cards (WhatsApp / Telegram style)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action 1: Add New Contact
                ContactActionTile(
                    icon = Icons.Default.PersonAdd,
                    title = "New Contact",
                    subtitle = "Add by phone number or PQC handle",
                    iconColor = QuantumCyan,
                    onClick = { showAddContactModal = true },
                    tag = "tile_new_contact"
                )

                // Action 2: New Group
                ContactActionTile(
                    icon = Icons.Default.GroupAdd,
                    title = "New Group Chat",
                    subtitle = "Create encrypted multi-node channel",
                    iconColor = TacticalEmerald,
                    onClick = onCreateGroupClick,
                    tag = "tile_new_group"
                )

                // Action 3: Sync Phonebook Address Book
                ContactActionTile(
                    icon = Icons.Default.Contacts,
                    title = "Sync Phone Contacts",
                    subtitle = "Discover contacts registered on Q-Crypt",
                    iconColor = WarningAmber,
                    onClick = onSyncPhonebookClick,
                    tag = "tile_sync_contacts"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSlate)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONTACTS ON Q-CRYPT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Contact List
            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No contacts found matching '$searchQuery'" else "No contacts added yet",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { contact ->
                        ContactListItem(
                            contact = contact,
                            onMessageClick = { onContactSelect(contact) },
                            onAudioCallClick = { onCallContactClick(contact, false) },
                            onVideoCallClick = { onCallContactClick(contact, true) }
                        )
                        HorizontalDivider(color = BorderDark, thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    // Modal Dialog: Add New Contact by Phone Number or Name
    if (showAddContactModal) {
        AddContactModalDialog(
            onDismiss = { showAddContactModal = false },
            onSubmit = { name, phone, key ->
                onAddContactSubmit(name, phone, key)
                showAddContactModal = false
            }
        )
    }
}

@Composable
private fun ContactActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        onClick = onClick,
        color = InnerBoxSlate,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
private fun ContactListItem(
    contact: ContactEntity,
    onMessageClick: () -> Unit,
    onAudioCallClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMessageClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("contact_item_${contact.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Circle with Status Indicator
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CardSlate)
                    .border(1.dp, BorderSlate, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantumCyan
                )
            }

            if (contact.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(TacticalEmerald)
                        .border(2.dp, ObsidianBlack, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name, Phone Number, Fingerprint
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (contact.phoneNumber.isNotBlank()) {
                Text(
                    text = contact.phoneNumber,
                    fontSize = 12.sp,
                    color = QuantumCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = TacticalEmerald,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "FP: ${contact.verifiedFingerprint}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Action Buttons: Message, Audio Call, Video Call
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onAudioCallClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_contact_call_audio_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Audio Call",
                    tint = QuantumCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onVideoCallClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_contact_call_video_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = TacticalEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onMessageClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_contact_message_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Send Message",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddContactModalDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, phoneNumber: String, publicKey: String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = QuantumCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Contact", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter phone number or node display name to add to your post-quantum contacts list.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Contact Name / Alias", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_add_contact_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    )
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone Number (e.g. +1 555-234-5678)", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_add_contact_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    )
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("PQC Node Key / QR (Optional)", color = TextMuted) },
                    singleLine = true,
                    placeholder = { Text("Auto-generated if left blank", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_add_contact_key"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = QuantumCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedContainerColor = InnerBoxSlate,
                        unfocusedContainerColor = InnerBoxSlate
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        onSubmit(nameInput, phoneInput, keyInput)
                    }
                },
                enabled = nameInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = QuantumCyan, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_submit_add_contact")
            ) {
                Text("Add & Message", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkSlate,
        shape = RoundedCornerShape(20.dp)
    )
}
