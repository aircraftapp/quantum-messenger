package com.example.data.repository

import com.example.crypto.QuantumCryptoEngine
import com.example.data.local.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class QuantumMessengerRepository(private val dao: QuantumDao) {

    val allChats: Flow<List<ChatEntity>> = dao.getAllChats()
    val allContacts: Flow<List<ContactEntity>> = dao.getAllContacts()
    val allCloudAccounts: Flow<List<CloudAccountEntity>> = dao.getAllCloudAccounts()
    val activeStatusStories: Flow<List<StatusStoryEntity>> = dao.getActiveStatusStories()

    suspend fun initializeStarterDataIfEmpty() {
        val currentChats = dao.getAllChats().first()
        if (currentChats.isEmpty()) {
            val satoshiContact = ContactEntity(
                id = "NODE-SATOSHI-9981",
                name = "Satoshi N. [Node]",
                phoneNumber = "+1 (800) 555-0100",
                pqcPublicKey = "PQC-KYBER1024-SATOSHI-KEY-991A",
                verifiedFingerprint = "7F-9A-3B-2C"
            )
            val aliceContact = ContactEntity(
                id = "NODE-ALICE-4420",
                name = "Alice Vance [Node]",
                phoneNumber = "+1 (555) 234-5678",
                pqcPublicKey = "PQC-KYBER1024-ALICE-KEY-220B",
                verifiedFingerprint = "1A-8B-9C-3E"
            )
            val elenaContact = ContactEntity(
                id = "NODE-ELENA-7712",
                name = "Elena Rostova",
                phoneNumber = "+1 (555) 019-2834",
                pqcPublicKey = "PQC-KYBER1024-ELENA-KEY-7712",
                verifiedFingerprint = "3C-11-22-FA"
            )
            val marcusContact = ContactEntity(
                id = "NODE-MARCUS-3390",
                name = "Marcus Brody",
                phoneNumber = "+1 (555) 014-3882",
                pqcPublicKey = "PQC-KYBER1024-MARCUS-KEY-3390",
                verifiedFingerprint = "9A-FE-88-01"
            )
            val squadGroup = ContactEntity(
                id = "GROUP-CIPHER-SQUAD",
                name = "Cipher Tactical Squad",
                phoneNumber = "+1 (555) 990-0000",
                pqcPublicKey = "PQC-GROUP-MULTIKEY-SHA384",
                verifiedFingerprint = "99-E4-11-F0"
            )

            dao.insertContact(satoshiContact)
            dao.insertContact(aliceContact)
            dao.insertContact(elenaContact)
            dao.insertContact(marcusContact)
            dao.insertContact(squadGroup)

            val chat1 = ChatEntity(
                id = "CHAT-SATOSHI",
                title = "Satoshi N.",
                isGroup = false,
                participantIdsCsv = satoshiContact.id,
                lastMessage = "Zero-knowledge verification complete. Post-quantum channel ready.",
                lastMessageTime = System.currentTimeMillis() - 120000,
                ephemeralSettingSeconds = 30L, // 30 seconds ephemeral timer
                securityFingerprint = "PQC-8F92-SATOSHI"
            )

            val chat2 = ChatEntity(
                id = "CHAT-ALICE",
                title = "Alice Vance",
                isGroup = false,
                participantIdsCsv = aliceContact.id,
                lastMessage = "Audio & video encryption verified using phone local compute.",
                lastMessageTime = System.currentTimeMillis() - 3600000,
                ephemeralSettingSeconds = 300L, // 5 min
                securityFingerprint = "PQC-220B-ALICE"
            )

            val chat3 = ChatEntity(
                id = "CHAT-CIPHER-SQUAD",
                title = "Cipher Tactical Squad (Group)",
                isGroup = true,
                participantIdsCsv = "${satoshiContact.id},${aliceContact.id}",
                lastMessage = "Squad E2EE file transfer channel initialized.",
                lastMessageTime = System.currentTimeMillis() - 86400000,
                ephemeralSettingSeconds = 0L,
                securityFingerprint = "PQC-GROUP-SQUAD"
            )

            dao.insertChat(chat1)
            dao.insertChat(chat2)
            dao.insertChat(chat3)

            // Insert initial messages for Chat 1
            val msg1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chat1.id,
                senderId = satoshiContact.id,
                senderName = satoshiContact.name,
                isFromMe = false,
                textContent = "Zero-knowledge handshake initiated.",
                encryptedPayload = QuantumCryptoEngine.encryptPostQuantum("Zero-knowledge handshake initiated.", satoshiContact.pqcPublicKey),
                timestamp = System.currentTimeMillis() - 300000,
                ephemeralDurationSeconds = 30L,
                expiresAtTimestamp = 0L
            )
            val msg2 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chat1.id,
                senderId = QuantumCryptoEngine.deviceNodeId,
                senderName = "Me",
                isFromMe = true,
                textContent = "Kyber-1024 key exchange confirmed. Zero server footprints.",
                encryptedPayload = QuantumCryptoEngine.encryptPostQuantum("Kyber-1024 key exchange confirmed.", satoshiContact.pqcPublicKey),
                timestamp = System.currentTimeMillis() - 120000,
                ephemeralDurationSeconds = 30L,
                expiresAtTimestamp = 0L
            )
            val msg3 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chat1.id,
                senderId = satoshiContact.id,
                senderName = satoshiContact.name,
                isFromMe = false,
                textContent = "Zero-knowledge verification complete. Post-quantum channel ready.",
                encryptedPayload = QuantumCryptoEngine.encryptPostQuantum("Zero-knowledge verification complete.", satoshiContact.pqcPublicKey),
                timestamp = System.currentTimeMillis() - 60000,
                ephemeralDurationSeconds = 30L,
                expiresAtTimestamp = 0L
            )

            dao.insertMessage(msg1)
            dao.insertMessage(msg2)
            dao.insertMessage(msg3)

            // Insert default cloud accounts
            val driveAccount = CloudAccountEntity(
                id = "CLOUD-GDRIVE",
                providerName = "Google Drive (Personal Cloud)",
                providerType = CloudProviderType.GOOGLE_DRIVE,
                accountEmailOrPath = "user.vault@drive.google.com",
                isConnected = true,
                isAutoSyncEnabled = true,
                lastSyncTimestamp = System.currentTimeMillis() - 1800000,
                totalBackupsCount = 3,
                storageUsedFormatted = "2.4 MB"
            )
            val zipExportAccount = CloudAccountEntity(
                id = "CLOUD-ZIP",
                providerName = "Encrypted Local File (.qpkg / .zip)",
                providerType = CloudProviderType.DOWNLOADABLE_ZIP,
                accountEmailOrPath = "/storage/emulated/0/Download/Quantum_Vault_Backup.zip",
                isConnected = true,
                isAutoSyncEnabled = false,
                lastSyncTimestamp = System.currentTimeMillis() - 7200000,
                totalBackupsCount = 1,
                storageUsedFormatted = "1.8 MB"
            )

            dao.insertCloudAccount(driveAccount)
            dao.insertCloudAccount(zipExportAccount)

            // Insert initial status stories
            val story1 = StatusStoryEntity(
                id = "STORY-SATOSHI-1",
                authorId = satoshiContact.id,
                authorName = satoshiContact.name,
                caption = "⚡ Kyber-1024 Post-Quantum key exchange test successful on local mesh node! Zero metadata logged.",
                bgGradientHex = "#0D1B2A",
                timestamp = System.currentTimeMillis() - 3600000,
                expiresAtTimestamp = System.currentTimeMillis() + 82800000L
            )
            val story2 = StatusStoryEntity(
                id = "STORY-ALICE-1",
                authorId = aliceContact.id,
                authorName = aliceContact.name,
                caption = "🔒 Ephemeral message shredder active! Auto-delete timer set to 5m for top secret chats.",
                bgGradientHex = "#1B263B",
                timestamp = System.currentTimeMillis() - 7200000,
                expiresAtTimestamp = System.currentTimeMillis() + 79200000L
            )
            dao.insertStatusStory(story1)
            dao.insertStatusStory(story2)
        }
    }

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return dao.getMessagesForChat(chatId)
    }

    fun getChatById(chatId: String): Flow<ChatEntity?> {
        return dao.getChatById(chatId)
    }

    suspend fun sendMessage(
        chatId: String,
        text: String,
        messageType: MessageType = MessageType.TEXT,
        mediaUri: String? = null,
        mediaSizeFormatted: String? = null,
        mediaDurationSeconds: Int = 0,
        ephemeralSeconds: Long = 0L
    ) {
        val encrypted = QuantumCryptoEngine.encryptPostQuantum(text, "RECIPIENT-PQC-KEY")
        val now = System.currentTimeMillis()
        val expiresAt = if (ephemeralSeconds > 0L) now + (ephemeralSeconds * 1000L) else 0L

        val newMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = QuantumCryptoEngine.deviceNodeId,
            senderName = "Me",
            isFromMe = true,
            textContent = text,
            encryptedPayload = encrypted,
            messageType = messageType,
            mediaUri = mediaUri,
            mediaSizeFormatted = mediaSizeFormatted,
            mediaDurationSeconds = mediaDurationSeconds,
            timestamp = now,
            ephemeralDurationSeconds = ephemeralSeconds,
            expiresAtTimestamp = expiresAt
        )

        dao.insertMessage(newMessage)

        // Update chat last message
        val summaryText = when (messageType) {
            MessageType.TEXT -> text
            MessageType.VOICE -> "🎤 Voice Note ($mediaDurationSeconds s)"
            MessageType.VIDEO -> "📹 Video Note ($mediaDurationSeconds s)"
            MessageType.FILE -> "📄 Shared Encrypted File ($mediaSizeFormatted)"
            MessageType.WALKIE_TALKIE -> text
        }

        val chat = dao.getChatById(chatId).first()
        if (chat != null) {
            val updatedChat = chat.copy(
                lastMessage = summaryText,
                lastMessageTime = now
            )
            dao.updateChat(updatedChat)
        }
    }

    suspend fun updateChatEphemeralTimer(chatId: String, ephemeralSeconds: Long) {
        dao.updateChatEphemeralTimer(chatId, ephemeralSeconds)
    }

    suspend fun purgeExpiredMessages() {
        dao.purgeExpiredEphemeralMessages(System.currentTimeMillis())
    }

    suspend fun createNewContactAndChat(nodeName: String, phoneNumber: String = "", nodePublicKey: String = ""): String {
        val contactId = "NODE-" + UUID.randomUUID().toString().take(8).uppercase()
        val contact = ContactEntity(
            id = contactId,
            name = nodeName,
            phoneNumber = phoneNumber,
            pqcPublicKey = if (nodePublicKey.isBlank()) "PQC-KYBER1024-KEY-" + UUID.randomUUID().toString().take(6) else nodePublicKey,
            verifiedFingerprint = "PQC-FP-" + UUID.randomUUID().toString().take(6).uppercase()
        )
        dao.insertContact(contact)

        val chatId = "CHAT-" + UUID.randomUUID().toString().take(8)
        val chat = ChatEntity(
            id = chatId,
            title = nodeName,
            isGroup = false,
            participantIdsCsv = contactId,
            lastMessage = if (phoneNumber.isNotBlank()) "Contact added ($phoneNumber). E2EE channel active." else "Node paired via zero-knowledge handshake.",
            lastMessageTime = System.currentTimeMillis(),
            securityFingerprint = contact.verifiedFingerprint
        )
        dao.insertChat(chat)
        return chatId
    }

    suspend fun getOrCreateChatForContact(contact: ContactEntity): String {
        val allChatsList = dao.getAllChats().first()
        val existingChat = allChatsList.find { it.participantIdsCsv == contact.id || it.title == contact.name }
        if (existingChat != null) {
            return existingChat.id
        }

        val newChatId = "CHAT-" + UUID.randomUUID().toString().take(8)
        val newChat = ChatEntity(
            id = newChatId,
            title = contact.name,
            isGroup = false,
            participantIdsCsv = contact.id,
            lastMessage = "Channel established with ${contact.name}.",
            lastMessageTime = System.currentTimeMillis(),
            securityFingerprint = contact.verifiedFingerprint
        )
        dao.insertChat(newChat)
        return newChatId
    }

    suspend fun importPhonebookContacts(): Int {
        val sampleContacts = listOf(
            ContactEntity(
                id = "NODE-ARIS-8812",
                name = "Dr. Aris Thorne",
                phoneNumber = "+1 (555) 082-1943",
                pqcPublicKey = "PQC-KYBER1024-ARIS-8812",
                verifiedFingerprint = "5B-99-44-DD",
                isOnline = true
            ),
            ContactEntity(
                id = "NODE-SARAH-5521",
                name = "Sarah Connor",
                phoneNumber = "+1 (555) 019-9238",
                pqcPublicKey = "PQC-KYBER1024-SARAH-5521",
                verifiedFingerprint = "AA-88-11-22",
                isOnline = false
            ),
            ContactEntity(
                id = "NODE-LOGAN-1092",
                name = "Logan Wright",
                phoneNumber = "+1 (555) 017-4401",
                pqcPublicKey = "PQC-KYBER1024-LOGAN-1092",
                verifiedFingerprint = "77-CC-33-88",
                isOnline = true
            ),
            ContactEntity(
                id = "NODE-ZOE-4102",
                name = "Zoe Miller",
                phoneNumber = "+1 (555) 012-7789",
                pqcPublicKey = "PQC-KYBER1024-ZOE-4102",
                verifiedFingerprint = "EE-44-99-11",
                isOnline = true
            )
        )
        for (c in sampleContacts) {
            dao.insertContact(c)
        }
        return sampleContacts.size
    }

    suspend fun createGroupChat(groupName: String, selectedContactIds: List<String>): String {
        val chatId = "GROUP-" + UUID.randomUUID().toString().take(8)
        val chat = ChatEntity(
            id = chatId,
            title = "$groupName (Group)",
            isGroup = true,
            participantIdsCsv = selectedContactIds.joinToString(","),
            lastMessage = "Encrypted group channel created.",
            lastMessageTime = System.currentTimeMillis(),
            securityFingerprint = "PQC-GROUP-MULTIKEY"
        )
        dao.insertChat(chat)
        return chatId
    }

    suspend fun addCloudAccount(accountName: String, providerType: CloudProviderType, emailOrPath: String) {
        val newAcc = CloudAccountEntity(
            id = "CLOUD-" + UUID.randomUUID().toString().take(8),
            providerName = accountName,
            providerType = providerType,
            accountEmailOrPath = emailOrPath,
            isConnected = true,
            isAutoSyncEnabled = true,
            lastSyncTimestamp = System.currentTimeMillis(),
            totalBackupsCount = 1,
            storageUsedFormatted = "1.2 MB"
        )
        dao.insertCloudAccount(newAcc)
    }

    suspend fun removeCloudAccount(account: CloudAccountEntity) {
        dao.deleteCloudAccount(account)
    }

    suspend fun syncCloudAccountNow(accountId: String) {
        dao.updateAccountLastSync(accountId, System.currentTimeMillis())
    }

    suspend fun updateChatWallpaperTheme(chatId: String, theme: String) {
        dao.updateChatWallpaperTheme(chatId, theme)
    }

    suspend fun toggleMessageReaction(messageId: String, currentReactions: String, emoji: String) {
        val existingList = currentReactions.split(",").filter { it.isNotBlank() }.toMutableList()
        if (existingList.contains(emoji)) {
            existingList.remove(emoji)
        } else {
            existingList.add(emoji)
        }
        dao.updateMessageReactions(messageId, existingList.joinToString(","))
    }

    suspend fun postStatusStory(caption: String, bgGradientHex: String = "#0D1B2A", mediaUri: String? = null) {
        val newStory = StatusStoryEntity(
            id = "STORY-" + UUID.randomUUID().toString().take(8),
            authorId = QuantumCryptoEngine.deviceNodeId,
            authorName = "My Node (Me)",
            caption = caption,
            mediaUri = mediaUri,
            bgGradientHex = bgGradientHex,
            timestamp = System.currentTimeMillis(),
            expiresAtTimestamp = System.currentTimeMillis() + 86400000L // 24 hours
        )
        dao.insertStatusStory(newStory)
    }

    suspend fun sendBroadcastMessage(targetChatIds: List<String>, messageText: String) {
        for (chatId in targetChatIds) {
            sendMessage(
                chatId = chatId,
                text = "📢 BROADCAST: $messageText",
                messageType = MessageType.TEXT
            )
        }
    }

    suspend fun createChannel(channelName: String, description: String, isPublic: Boolean): String {
        val channelId = "CHANNEL-" + UUID.randomUUID().toString().take(8)
        val channelChat = ChatEntity(
            id = channelId,
            title = "📢 $channelName",
            isGroup = false,
            isChannel = true,
            channelSubscriberCount = 1420,
            participantIdsCsv = QuantumCryptoEngine.deviceNodeId,
            lastMessage = "Channel '$channelName' initialized. $description",
            lastMessageTime = System.currentTimeMillis(),
            securityFingerprint = "PQC-CHANNEL-BROADCAST"
        )
        dao.insertChat(channelChat)

        // Initial announcement message
        sendMessage(
            chatId = channelId,
            text = "🎉 Welcome to $channelName! $description",
            messageType = MessageType.TEXT
        )

        return channelId
    }

    fun searchContacts(query: String): Flow<List<ContactEntity>> {
        return if (query.isBlank()) {
            dao.getAllContacts()
        } else {
            dao.searchContacts(query.trim())
        }
    }

    suspend fun bulkArchiveChats(chatIds: List<String>, isArchived: Boolean) {
        dao.updateChatsArchivedStatus(chatIds, isArchived)
    }

    suspend fun bulkDeleteChats(chatIds: List<String>) {
        dao.deleteChats(chatIds)
        dao.deleteMessagesForChats(chatIds)
    }

    suspend fun triggerEmergencyRemoteWipe() {
        dao.deleteAllMessages()
        dao.deleteAllChats()
        dao.deleteAllContacts()
        dao.deleteAllStatusStories()
    }

    suspend fun saveChatDraft(chatId: String, draftText: String) {
        dao.updateChatDraft(chatId, draftText)
    }

    suspend fun setContactBlocked(contactId: String, isBlocked: Boolean) {
        dao.updateContactBlockedStatus(contactId, isBlocked)
    }

    suspend fun setContactPresence(contactId: String, presenceStatus: String, isOnline: Boolean) {
        dao.updateContactPresenceStatus(contactId, presenceStatus, isOnline)
    }

    suspend fun receiveDirectP2pMessage(
        senderNodeId: String,
        senderName: String,
        chatId: String,
        encryptedPayload: String,
        pqcPublicKey: String? = null
    ) {
        val allContacts = dao.getAllContactsDirect()
        val senderContact = allContacts.find { it.id == senderNodeId || it.name.equals(senderName, ignoreCase = true) }
        if (senderContact != null && senderContact.isBlocked) {
            // Drop P2P message from blocked sender
            return
        }

        val decryptedText = QuantumCryptoEngine.decryptPostQuantum(encryptedPayload)
        val now = System.currentTimeMillis()

        val allChatsList = dao.getAllChats().first()
        var targetChat = allChatsList.find { it.id == chatId || it.participantIdsCsv.contains(senderNodeId) }

        if (targetChat == null) {
            val contactId = if (senderNodeId.isNotBlank()) senderNodeId else "NODE-${UUID.randomUUID().toString().take(6)}"
            val newContact = ContactEntity(
                id = contactId,
                name = senderName,
                phoneNumber = "",
                pqcPublicKey = pqcPublicKey ?: "PQC-KYBER1024-KEY-P2P",
                verifiedFingerprint = "PQC-FP-P2P"
            )
            dao.insertContact(newContact)

            val newChatId = if (chatId.isNotBlank()) chatId else "CHAT-${UUID.randomUUID().toString().take(8)}"
            targetChat = ChatEntity(
                id = newChatId,
                title = senderName,
                isGroup = false,
                participantIdsCsv = contactId,
                lastMessage = decryptedText,
                lastMessageTime = now,
                securityFingerprint = newContact.verifiedFingerprint
            )
            dao.insertChat(targetChat)
        }

        val isWalkieTalkie = pqcPublicKey?.startsWith("WALKIE_TALKIE::") == true
        val walkieParts = if (isWalkieTalkie) pqcPublicKey?.split("::") ?: emptyList() else emptyList()
        val walkieChannel = walkieParts.getOrNull(1) ?: "CH-01"
        val walkieDur = walkieParts.getOrNull(2)?.toIntOrNull() ?: 3

        val msgType = if (isWalkieTalkie) MessageType.WALKIE_TALKIE else MessageType.TEXT
        val displayText = if (isWalkieTalkie) "📻 WALKIE-TALKIE TRANSMISSION • $walkieChannel (${walkieDur}s) - OVER & OUT" else decryptedText

        val newMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = targetChat.id,
            senderId = senderNodeId,
            senderName = senderName,
            isFromMe = false,
            textContent = displayText,
            encryptedPayload = encryptedPayload,
            messageType = msgType,
            mediaDurationSeconds = if (isWalkieTalkie) walkieDur else 0,
            timestamp = now,
            ephemeralDurationSeconds = targetChat.ephemeralSettingSeconds,
            expiresAtTimestamp = if (targetChat.ephemeralSettingSeconds > 0) now + (targetChat.ephemeralSettingSeconds * 1000L) else 0L
        )

        dao.insertMessage(newMessage)

        val updatedChat = targetChat.copy(
            lastMessage = displayText,
            lastMessageTime = now
        )
        dao.updateChat(updatedChat)
    }

    suspend fun updateContactTag(contactId: String, newTag: String) {
        dao.updateContactTag(contactId, newTag)
    }

    fun searchMessagesInChat(chatId: String, query: String): Flow<List<MessageEntity>> {
        return dao.searchMessagesInChat(chatId, query)
    }

    suspend fun exportSingleChatEncrypted(chatId: String, passphrase: String): String {
        val chat = dao.getChatById(chatId).first() ?: return ""
        val messages = dao.getMessagesForChat(chatId).first()
        val jsonStringBuilder = StringBuilder()
        jsonStringBuilder.append("{\"chatId\":\"${chat.id}\",\"title\":\"${chat.title}\",\"exportedAt\":${System.currentTimeMillis()},\"messages\":[")
        messages.forEachIndexed { index, msg ->
            jsonStringBuilder.append("{\"id\":\"${msg.id}\",\"sender\":\"${msg.senderName}\",\"text\":\"${msg.textContent.replace("\"", "\\\"")}\",\"timestamp\":${msg.timestamp}}")
            if (index < messages.size - 1) jsonStringBuilder.append(",")
        }
        jsonStringBuilder.append("]}")
        val rawJson = jsonStringBuilder.toString()
        val encryptedPayload = QuantumCryptoEngine.encryptPostQuantum(rawJson, "PASSPHRASE::$passphrase")
        
        // Record backup in cloud account table or return path
        val backupAcc = CloudAccountEntity(
            id = "CHAT_EXPORT_" + UUID.randomUUID().toString().take(6),
            providerName = "Encrypted Chat Backup (${chat.title})",
            providerType = CloudProviderType.DOWNLOADABLE_ZIP,
            accountEmailOrPath = "/storage/emulated/0/Download/Quantum_Chat_${chat.id}.qchat",
            isConnected = true,
            isAutoSyncEnabled = false,
            lastSyncTimestamp = System.currentTimeMillis(),
            totalBackupsCount = 1,
            storageUsedFormatted = "${(rawJson.length / 1024.0).toString().take(4)} KB"
        )
        dao.insertCloudAccount(backupAcc)
        return backupAcc.accountEmailOrPath
    }
}
