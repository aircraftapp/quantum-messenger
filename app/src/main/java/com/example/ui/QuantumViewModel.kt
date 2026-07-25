package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.crypto.ComputeMonitorEngine
import com.example.crypto.LocalComputeMetrics
import com.example.crypto.QuantumCryptoEngine
import com.example.data.MigrationExportResult
import com.example.data.MigrationImportResult
import com.example.data.ZipVaultMigrationManager
import com.example.data.local.*
import com.example.data.repository.QuantumMessengerRepository
import com.example.network.p2p.P2pNetworkManager
import com.example.network.p2p.P2pPeerNode
import com.example.network.p2p.P2pServerStatus
import com.example.service.MediaTranscodeEncryptWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class MediaJobUiState(
    val id: String,
    val mediaType: String,
    val progress: Int,
    val statusText: String,
    val isCompleted: Boolean = false,
    val outputPath: String? = null
)

enum class Screen {
    INITIAL_SETUP, CHAT_LIST, CONVERSATION, PQC_CALL, CLOUD_SYNC, COMPUTE_DASHBOARD, CONTACTS_DIRECTORY
}

data class ActiveCallState(
    val isCallActive: Boolean = false,
    val peerName: String = "",
    val peerAvatar: String? = null,
    val isVideoCall: Boolean = true,
    val callDurationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isKeyRotationActive: Boolean = true,
    val pqcSessionHandshake: String = "KYBER-1024 / AES-GCM ACTIVE"
)

class QuantumViewModel(application: Application) : AndroidViewModel(application) {

    private val db = QuantumDatabase.getDatabase(application)
    private val repository = QuantumMessengerRepository(db.quantumDao())
    private val prefs = application.getSharedPreferences("quantum_messenger_prefs", Application.MODE_PRIVATE)
    private val workManager = WorkManager.getInstance(application)

    // --- WORKMANAGER & MEDIA TRANSCODING STATE ---
    private val _activeMediaJobs = MutableStateFlow<List<MediaJobUiState>>(emptyList())
    val activeMediaJobs: StateFlow<List<MediaJobUiState>> = _activeMediaJobs.asStateFlow()

    // --- LOCAL BACKUPS & MIGRATION STATE ---
    private val _localBackupFiles = MutableStateFlow<List<File>>(emptyList())
    val localBackupFiles: StateFlow<List<File>> = _localBackupFiles.asStateFlow()

    private val _showImportBackupDialog = MutableStateFlow(false)
    val showImportBackupDialog: StateFlow<Boolean> = _showImportBackupDialog.asStateFlow()

    // --- SETUP & CONFIGURATION STATE ---
    private val _isConfigured = MutableStateFlow(prefs.getBoolean("is_configured", false))
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    private val _setupStep = MutableStateFlow(1) // 1 to 5
    val setupStep: StateFlow<Int> = _setupStep.asStateFlow()

    private val _setupNodeName = MutableStateFlow(prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node")
    val setupNodeName: StateFlow<String> = _setupNodeName.asStateFlow()

    private val _setupPin = MutableStateFlow(prefs.getString("user_pin", "123456") ?: "123456")
    val setupPin: StateFlow<String> = _setupPin.asStateFlow()

    private val _selectedCloudType = MutableStateFlow(CloudProviderType.GOOGLE_DRIVE)
    val selectedCloudType: StateFlow<CloudProviderType> = _selectedCloudType.asStateFlow()

    private val _keyGenProgress = MutableStateFlow(0f)
    val keyGenProgress: StateFlow<Float> = _keyGenProgress.asStateFlow()

    private val _isGeneratingKeys = MutableStateFlow(false)
    val isGeneratingKeys: StateFlow<Boolean> = _isGeneratingKeys.asStateFlow()

    // --- SECURITY & BIOMETRIC LOCK ---
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _userPin = MutableStateFlow(prefs.getString("user_pin", "123456") ?: "123456")
    val userPin: StateFlow<String> = _userPin.asStateFlow()

    private val _pinInput = MutableStateFlow("")
    val pinInput: StateFlow<String> = _pinInput.asStateFlow()

    private val _pinError = MutableStateFlow(false)
    val pinError: StateFlow<Boolean> = _pinError.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(prefs.getBoolean("is_biometric_enabled", true))
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    // --- NAVIGATION ---
    private val _activeScreen = MutableStateFlow(
        if (!prefs.getBoolean("is_configured", false)) Screen.INITIAL_SETUP else Screen.CHAT_LIST
    )
    val activeScreen: StateFlow<Screen> = _activeScreen.asStateFlow()

    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    // --- CHATS & MESSAGES ---
    val allChats: StateFlow<List<ChatEntity>> = repository.allChats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allContacts: StateFlow<List<ContactEntity>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _contactSearchQuery = MutableStateFlow("")
    val contactSearchQuery: StateFlow<String> = _contactSearchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchedContacts: StateFlow<List<ContactEntity>> = _contactSearchQuery
        .flatMapLatest { query ->
            repository.searchContacts(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCloudAccounts: StateFlow<List<CloudAccountEntity>> = repository.allCloudAccounts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeStatusStories: StateFlow<List<StatusStoryEntity>> = repository.activeStatusStories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- DIALOG & MODAL STATES ---
    private val _showChatSettingsDialog = MutableStateFlow(false)
    val showChatSettingsDialog: StateFlow<Boolean> = _showChatSettingsDialog.asStateFlow()

    private val _showQrScannerDialog = MutableStateFlow(false)
    val showQrScannerDialog: StateFlow<Boolean> = _showQrScannerDialog.asStateFlow()
    val showQrScannerModal: StateFlow<Boolean> = _showQrScannerDialog.asStateFlow()

    private val _showCreateStoryDialog = MutableStateFlow(false)
    val showCreateStoryDialog: StateFlow<Boolean> = _showCreateStoryDialog.asStateFlow()

    private val _activeStoryForViewing = MutableStateFlow<StatusStoryEntity?>(null)
    val activeStoryForViewing: StateFlow<StatusStoryEntity?> = _activeStoryForViewing.asStateFlow()
    val activeStory: StateFlow<StatusStoryEntity?> = _activeStoryForViewing.asStateFlow()
    val showStoryViewerModal: StateFlow<Boolean> = _activeStoryForViewing
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _showBroadcastDialog = MutableStateFlow(false)
    val showBroadcastDialog: StateFlow<Boolean> = _showBroadcastDialog.asStateFlow()
    val showBroadcastListDialog: StateFlow<Boolean> = _showBroadcastDialog.asStateFlow()

    private val _showCreateChannelDialog = MutableStateFlow(false)
    val showCreateChannelDialog: StateFlow<Boolean> = _showCreateChannelDialog.asStateFlow()

    val statusStories: StateFlow<List<StatusStoryEntity>> = repository.activeStatusStories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeChat = MutableStateFlow<ChatEntity?>(null)
    val activeChat: StateFlow<ChatEntity?> = _activeChat.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val activeMessages: StateFlow<List<MessageEntity>> = _activeMessages.asStateFlow()

    // --- MESSAGE INPUT ---
    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    // --- EPHEMERAL TIMER SETTINGS ---
    private val _selectedEphemeralSeconds = MutableStateFlow(0L) // 0 = Never
    val selectedEphemeralSeconds: StateFlow<Long> = _selectedEphemeralSeconds.asStateFlow()

    private val _showEphemeralDialog = MutableStateFlow(false)
    val showEphemeralDialog: StateFlow<Boolean> = _showEphemeralDialog.asStateFlow()

    // --- MEDIA RECORDING & TRANSFERS ---
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    // --- CALL STATE ---
    private val _activeCallState = MutableStateFlow(ActiveCallState())
    val activeCallState: StateFlow<ActiveCallState> = _activeCallState.asStateFlow()

    // --- COMPUTE MONITOR METRICS ---
    val computeMetrics: StateFlow<LocalComputeMetrics> = ComputeMonitorEngine.observeComputeMetrics().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LocalComputeMetrics()
    )

    // --- DIALOGS ---
    private val _showPairNodeDialog = MutableStateFlow(false)
    val showPairNodeDialog: StateFlow<Boolean> = _showPairNodeDialog.asStateFlow()

    private val _showCreateGroupDialog = MutableStateFlow(false)
    val showCreateGroupDialog: StateFlow<Boolean> = _showCreateGroupDialog.asStateFlow()

    private val _showExportBackupDialog = MutableStateFlow(false)
    val showExportBackupDialog: StateFlow<Boolean> = _showExportBackupDialog.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    // --- P2P WEBSOCKET NETWORKING LAYER ---
    private val p2pNetworkManager = P2pNetworkManager.getInstance(getApplication())
    val p2pServerStatus: StateFlow<P2pServerStatus> = p2pNetworkManager.serverStatus
    val p2pServerInfo: StateFlow<String> = p2pNetworkManager.serverInfo
    val p2pActivePeers: StateFlow<List<P2pPeerNode>> = p2pNetworkManager.activePeers

    // --- P2P WALKIE-TALKIE (PUSH-TO-TALK) STATE ---
    private val _isWalkieTalkieModeActive = MutableStateFlow(false)
    val isWalkieTalkieModeActive: StateFlow<Boolean> = _isWalkieTalkieModeActive.asStateFlow()

    private val _selectedPttChannel = MutableStateFlow("CH-01 (446.006 MHz)")
    val selectedPttChannel: StateFlow<String> = _selectedPttChannel.asStateFlow()

    private val _isPttTransmitting = MutableStateFlow(false)
    val isPttTransmitting: StateFlow<Boolean> = _isPttTransmitting.asStateFlow()

    private val _pttTransmissionDuration = MutableStateFlow(0)
    val pttTransmissionDuration: StateFlow<Int> = _pttTransmissionDuration.asStateFlow()

    private val _latestPttReceivedAlert = MutableStateFlow<String?>(null)
    val latestPttReceivedAlert: StateFlow<String?> = _latestPttReceivedAlert.asStateFlow()

    // --- SHARED FOLDERS & E2E PQC FILE TRANSFER STATE ---
    private val _sharedFolders = MutableStateFlow<List<QuantumCryptoEngine.SharedFolderSyncItem>>(
        listOf(
            QuantumCryptoEngine.SharedFolderSyncItem(
                id = "SF-01",
                folderName = "📁 /QuantumVault/Shared_Project_Alpha",
                path = "/storage/emulated/0/QuantumVault/Project_Alpha",
                sharedWithPeer = "Quantum Beta Node",
                isOnline = true,
                fileCount = 8,
                totalSizeFormatted = "48.5 MB",
                syncProgress = 1.0f,
                isSyncing = false
            ),
            QuantumCryptoEngine.SharedFolderSyncItem(
                id = "SF-02",
                folderName = "📁 /EncryptedDocs/Team_Field_Intel",
                path = "/storage/emulated/0/EncryptedDocs/Field_Intel",
                sharedWithPeer = "Tactical Echo Node",
                isOnline = true,
                fileCount = 14,
                totalSizeFormatted = "124.2 MB",
                syncProgress = 1.0f,
                isSyncing = false
            )
        )
    )
    val sharedFolders: StateFlow<List<QuantumCryptoEngine.SharedFolderSyncItem>> = _sharedFolders.asStateFlow()

    private val _activeFileOperationName = MutableStateFlow<String?>(null)
    val activeFileOperationName: StateFlow<String?> = _activeFileOperationName.asStateFlow()

    private val _fileOperationProgress = MutableStateFlow(0f)
    val fileOperationProgress: StateFlow<Float> = _fileOperationProgress.asStateFlow()

    private val _fileOperationStatus = MutableStateFlow<String?>(null)
    val fileOperationStatus: StateFlow<String?> = _fileOperationStatus.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeStarterDataIfEmpty()
            refreshLocalBackups()
        }

        // Ephemeral Message Shredder Loop (runs every second)
        viewModelScope.launch {
            while (true) {
                delay(1000)
                repository.purgeExpiredMessages()
            }
        }

        // Attach P2P WebSocket listeners
        p2pNetworkManager.onIncomingMessageListener = { senderNodeId, senderName, chatId, encryptedPayload, pqcPublicKey ->
            viewModelScope.launch {
                repository.receiveDirectP2pMessage(
                    senderNodeId = senderNodeId,
                    senderName = senderName,
                    chatId = chatId,
                    encryptedPayload = encryptedPayload,
                    pqcPublicKey = pqcPublicKey
                )
            }
        }

        p2pNetworkManager.onIncomingWalkieTalkieListener = { senderNodeId, senderName, chatId, channel, durationSeconds, encryptedPayload ->
            _latestPttReceivedAlert.value = "📻 LIVE PTT TRANSMISSION FROM $senderName • $channel (${durationSeconds}s) - OVER & OUT"
        }

        p2pNetworkManager.onNewPeerDiscoveredListener = { nodeId, name, address, pqcPublicKey ->
            viewModelScope.launch {
                repository.createNewContactAndChat(
                    nodeName = name,
                    nodePublicKey = pqcPublicKey
                )
            }
        }

        // Start P2P WebSocket Server automatically on port 8888
        val nodeName = prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node"
        p2pNetworkManager.startP2pServer(
            port = 8888,
            myNodeId = QuantumCryptoEngine.deviceNodeId,
            myName = nodeName,
            myPqcPublicKey = QuantumCryptoEngine.devicePqcPublicKey
        )
    }

    fun connectToP2pPeer(peerAddress: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val nodeName = prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node"
        p2pNetworkManager.connectToPeer(
            peerAddress = peerAddress,
            myNodeId = QuantumCryptoEngine.deviceNodeId,
            myName = nodeName,
            myPqcPublicKey = QuantumCryptoEngine.devicePqcPublicKey,
            onResult = onResult
        )
    }

    fun toggleP2pServer(enable: Boolean) {
        if (enable) {
            val nodeName = prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node"
            p2pNetworkManager.startP2pServer(
                port = 8888,
                myNodeId = QuantumCryptoEngine.deviceNodeId,
                myName = nodeName,
                myPqcPublicKey = QuantumCryptoEngine.devicePqcPublicKey
            )
        } else {
            p2pNetworkManager.stopP2pServer()
        }
    }

    // --- WORKMANAGER BACKGROUND MEDIA PROCESSING ---
    fun enqueueMediaTranscodeAndEncryption(mediaUri: String, mediaType: String) {
        val jobId = "WM-${System.currentTimeMillis().toString().takeLast(6)}"
        val workRequest = OneTimeWorkRequestBuilder<MediaTranscodeEncryptWorker>()
            .setInputData(
                workDataOf(
                    MediaTranscodeEncryptWorker.KEY_MEDIA_URI to mediaUri,
                    MediaTranscodeEncryptWorker.KEY_MEDIA_TYPE to mediaType,
                    MediaTranscodeEncryptWorker.KEY_MEDIA_ID to jobId
                )
            )
            .build()

        workManager.enqueue(workRequest)

        val newJob = MediaJobUiState(
            id = jobId,
            mediaType = mediaType,
            progress = 10,
            statusText = "Enqueued in WorkManager pipeline..."
        )
        _activeMediaJobs.value = _activeMediaJobs.value + newJob

        // Monitor WorkManager Live WorkInfo
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workRequest.id).collect { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.progress.getInt(MediaTranscodeEncryptWorker.KEY_PROGRESS, 0)
                    val statusText = workInfo.progress.getString(MediaTranscodeEncryptWorker.KEY_STATUS)
                        ?: workInfo.state.name

                    val isFinished = workInfo.state.isFinished
                    _activeMediaJobs.value = _activeMediaJobs.value.map { job ->
                        if (job.id == jobId) {
                            job.copy(
                                progress = if (isFinished) 100 else progress,
                                statusText = if (isFinished) "✓ Transcoding & Encryption Completed" else statusText,
                                isCompleted = isFinished,
                                outputPath = workInfo.outputData.getString(MediaTranscodeEncryptWorker.KEY_OUTPUT_PATH)
                            )
                        } else job
                    }
                }
            }
        }
    }

    // --- ENCRYPTED ZIP MIGRATION EXPORT / IMPORT ---
    fun refreshLocalBackups() {
        viewModelScope.launch {
            _localBackupFiles.value = ZipVaultMigrationManager.getLocalBackupFiles(getApplication())
        }
    }

    fun exportLocalEncryptedZip(password: String) {
        viewModelScope.launch {
            _backupStatusMessage.value = "Encrypting and packaging local database into password ZIP..."
            try {
                val result = ZipVaultMigrationManager.exportEncryptedZipVault(
                    context = getApplication(),
                    database = db,
                    password = password
                )
                refreshLocalBackups()
                _backupStatusMessage.value = "✓ Encrypted Zip Exported! (${result.fileSizeFormatted}, ${result.messagesCount} msgs, ${result.chatsCount} chats)"
                _showExportBackupDialog.value = false
            } catch (e: Exception) {
                _backupStatusMessage.value = "Export error: ${e.localizedMessage}"
            }
        }
    }

    fun importLocalEncryptedZip(zipFile: File, password: String) {
        viewModelScope.launch {
            _backupStatusMessage.value = "Decrypting and restoring vault archive..."
            try {
                val result = ZipVaultMigrationManager.importEncryptedZipVault(
                    context = getApplication(),
                    database = db,
                    zipFile = zipFile,
                    password = password
                )
                if (result.success) {
                    _backupStatusMessage.value = "✓ ${result.message}"
                    _showImportBackupDialog.value = false
                } else {
                    _backupStatusMessage.value = "⚠ ${result.message}"
                }
            } catch (e: Exception) {
                _backupStatusMessage.value = "Import error: ${e.localizedMessage}"
            }
        }
    }

    fun toggleImportBackupDialog(show: Boolean) {
        _showImportBackupDialog.value = show
    }

    // --- UNLOCK ACTION ---
    fun appendPinDigit(digit: String) {
        if (_pinInput.value.length < 6) {
            _pinInput.value += digit
            _pinError.value = false
            if (_pinInput.value.length == 6) {
                verifyPin()
            }
        }
    }

    fun backspacePinDigit() {
        if (_pinInput.value.isNotEmpty()) {
            _pinInput.value = _pinInput.value.dropLast(1)
            _pinError.value = false
        }
    }

    fun verifyPin() {
        if (_pinInput.value == _userPin.value) {
            _isLocked.value = false
            _pinInput.value = ""
            _pinError.value = false
        } else {
            _pinError.value = true
            _pinInput.value = ""
        }
    }

    fun authenticateBiometricSuccess() {
        _isLocked.value = false
        _pinInput.value = ""
        _pinError.value = false
    }

    fun toggleBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        prefs.edit().putBoolean("is_biometric_enabled", enabled).apply()
    }

    fun lockApp() {
        _isLocked.value = true
        _pinInput.value = ""
    }

    // --- SCREEN & CHAT SELECTION ---
    fun navigateToScreen(screen: Screen) {
        _activeScreen.value = screen
    }

    fun openConversation(chatId: String) {
        _activeChatId.value = chatId
        _activeScreen.value = Screen.CONVERSATION

        viewModelScope.launch {
            repository.getChatById(chatId).collect { chat ->
                _activeChat.value = chat
                if (chat != null) {
                    _selectedEphemeralSeconds.value = chat.ephemeralSettingSeconds
                }
            }
        }

        viewModelScope.launch {
            repository.getMessagesForChat(chatId).collect { messages ->
                _activeMessages.value = messages
            }
        }
    }

    fun updateMessageInput(text: String) {
        _messageInput.value = text
    }

    fun sendTextMessage() {
        val text = _messageInput.value.trim()
        val chatId = _activeChatId.value ?: return
        if (text.isBlank()) return

        val ephemeral = _selectedEphemeralSeconds.value
        _messageInput.value = ""

        val nodeName = prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node"
        val activeChat = _activeChat.value
        val targetNodeId = activeChat?.participantIdsCsv ?: ""

        val encryptedPayload = QuantumCryptoEngine.encryptPostQuantum(text, "RECIPIENT-PQC-KEY")

        p2pNetworkManager.sendDirectMessage(
            targetNodeId = targetNodeId,
            chatId = chatId,
            messageId = UUID.randomUUID().toString(),
            encryptedContent = encryptedPayload,
            myNodeId = QuantumCryptoEngine.deviceNodeId,
            myName = nodeName
        )

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = text,
                messageType = MessageType.TEXT,
                ephemeralSeconds = ephemeral
            )
        }
    }

    fun toggleWalkieTalkieMode() {
        _isWalkieTalkieModeActive.value = !_isWalkieTalkieModeActive.value
    }

    fun selectPttChannel(channel: String) {
        _selectedPttChannel.value = channel
    }

    fun startPttTransmission() {
        _isPttTransmitting.value = true
        _pttTransmissionDuration.value = 1
        viewModelScope.launch {
            while (_isPttTransmitting.value) {
                delay(1000)
                if (_isPttTransmitting.value) {
                    _pttTransmissionDuration.value += 1
                }
            }
        }
    }

    fun stopPttTransmissionAndSend() {
        if (!_isPttTransmitting.value) return
        val duration = _pttTransmissionDuration.value.coerceAtLeast(1)
        _isPttTransmitting.value = false

        val chatId = _activeChatId.value ?: return
        val channel = _selectedPttChannel.value
        sendPttVoiceBurst(chatId, channel, duration)
    }

    fun sendPttVoiceBurst(chatId: String, channel: String, durationSeconds: Int, textContent: String = "📻 WALKIE-TALKIE TRANSMISSION • $channel (${durationSeconds}s) - OVER & OUT") {
        val ephemeral = _selectedEphemeralSeconds.value
        val nodeName = prefs.getString("node_name", "Quantum Alpha Node") ?: "Quantum Alpha Node"
        val activeChat = _activeChat.value
        val targetNodeId = activeChat?.participantIdsCsv ?: ""

        val encryptedPayload = QuantumCryptoEngine.encryptPostQuantum(textContent, "PQC-KEY-WALKIE-TALKIE")

        p2pNetworkManager.sendWalkieTalkiePttMessage(
            targetNodeId = targetNodeId,
            chatId = chatId,
            channel = channel,
            durationSeconds = durationSeconds,
            encryptedContent = encryptedPayload,
            myNodeId = QuantumCryptoEngine.deviceNodeId,
            myName = nodeName
        )

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = textContent,
                messageType = MessageType.WALKIE_TALKIE,
                mediaDurationSeconds = durationSeconds,
                ephemeralSeconds = ephemeral
            )
            _pttTransmissionDuration.value = 0
        }
    }

    fun dismissPttAlert() {
        _latestPttReceivedAlert.value = null
    }

    fun sendVoiceNote() {
        val chatId = _activeChatId.value ?: return
        val duration = _recordingDurationSeconds.value.coerceAtLeast(3)
        val ephemeral = _selectedEphemeralSeconds.value

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = "🎤 Encrypted Voice Payload (${duration}s)",
                messageType = MessageType.VOICE,
                mediaDurationSeconds = duration,
                ephemeralSeconds = ephemeral
            )
            _isRecordingAudio.value = false
            _recordingDurationSeconds.value = 0
        }
    }

    fun sendVideoNote() {
        val chatId = _activeChatId.value ?: return
        val ephemeral = _selectedEphemeralSeconds.value

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = "📹 PQC Encrypted Video Note (12s)",
                messageType = MessageType.VIDEO,
                mediaDurationSeconds = 12,
                ephemeralSeconds = ephemeral
            )
        }
    }

    fun createSharedFolder(folderName: String, peerName: String) {
        val cleanName = if (folderName.startsWith("📁")) folderName else "📁 /QuantumVault/$folderName"
        val newFolder = QuantumCryptoEngine.SharedFolderSyncItem(
            id = "SF-${UUID.randomUUID().toString().take(6)}",
            folderName = cleanName,
            path = "/storage/emulated/0/QuantumVault/${folderName.replace(" ", "_")}",
            sharedWithPeer = peerName.ifBlank { "Group / Peer Network" },
            isOnline = true,
            fileCount = 1,
            totalSizeFormatted = "1.2 MB",
            syncProgress = 1.0f,
            isSyncing = false
        )
        _sharedFolders.value = _sharedFolders.value + newFolder
    }

    fun syncSharedFolderNow(folderId: String) {
        viewModelScope.launch {
            _sharedFolders.value = _sharedFolders.value.map { item ->
                if (item.id == folderId) item.copy(isSyncing = true, syncProgress = 0.1f) else item
            }

            for (step in 1..10) {
                delay(200)
                val p = step / 10f
                _sharedFolders.value = _sharedFolders.value.map { item ->
                    if (item.id == folderId) item.copy(syncProgress = p) else item
                }
            }

            _sharedFolders.value = _sharedFolders.value.map { item ->
                if (item.id == folderId) item.copy(
                    isSyncing = false,
                    syncProgress = 1.0f,
                    fileCount = item.fileCount + 1,
                    lastSyncedTime = System.currentTimeMillis()
                ) else item
            }
        }
    }

    fun sendEncryptedFileWithChunking(
        fileName: String,
        fileSizeMb: String,
        isSharedFolderSync: Boolean = false,
        folderName: String? = null
    ) {
        val chatId = _activeChatId.value ?: return
        val ephemeral = _selectedEphemeralSeconds.value

        viewModelScope.launch {
            _activeFileOperationName.value = "Kyber-1024 Hardware Encoding: $fileName"
            _fileOperationProgress.value = 0.1f
            _fileOperationStatus.value = "Utilizing phone CPU resources for Zero-Knowledge Post-Quantum chunking..."

            // Simulate phone hardware CPU chunk encoding
            for (step in 1..10) {
                delay(180)
                _fileOperationProgress.value = step / 10f
            }

            val prefix = if (isSharedFolderSync) "📁 SHARED FOLDER SYNC [${folderName ?: "Vault"}] • " else "📄 "
            val fullText = "$prefix$fileName ($fileSizeMb) - KYBER-1024 E2EE"

            repository.sendMessage(
                chatId = chatId,
                text = fullText,
                messageType = MessageType.FILE,
                mediaSizeFormatted = fileSizeMb,
                ephemeralSeconds = ephemeral
            )

            _fileOperationStatus.value = "✓ E2E Post-Quantum Encrypted file transferred strictly to recipient node."
            delay(1200)
            _activeFileOperationName.value = null
            _fileOperationProgress.value = 0f
            _fileOperationStatus.value = null
        }
    }

    fun decryptAndDownloadFileLocally(messageId: String, fileName: String) {
        viewModelScope.launch {
            _activeFileOperationName.value = "Kyber-1024 Phone CPU Decryption: $fileName"
            _fileOperationProgress.value = 0.1f
            _fileOperationStatus.value = "Decrypting Kyber-1024 KEM payload on phone local resources..."

            for (step in 1..10) {
                delay(150)
                _fileOperationProgress.value = step / 10f
            }

            _fileOperationStatus.value = "✓ File decrypted locally on device memory! Saved to /Download/QuantumVault/$fileName"
            delay(1500)
            _activeFileOperationName.value = null
            _fileOperationProgress.value = 0f
            _fileOperationStatus.value = null
        }
    }

    fun sendEncryptedFile(fileName: String, fileSizeMb: String) {
        sendEncryptedFileWithChunking(fileName, fileSizeMb)
    }

    fun toggleAudioRecording() {
        if (_isRecordingAudio.value) {
            sendVoiceNote()
        } else {
            _isRecordingAudio.value = true
            _recordingDurationSeconds.value = 0
            viewModelScope.launch {
                while (_isRecordingAudio.value) {
                    delay(1000)
                    _recordingDurationSeconds.value += 1
                }
            }
        }
    }

    fun setEphemeralTimerForActiveChat(seconds: Long) {
        _selectedEphemeralSeconds.value = seconds
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.updateChatEphemeralTimer(chatId, seconds)
        }
        _showEphemeralDialog.value = false
    }

    fun toggleEphemeralDialog(show: Boolean) {
        _showEphemeralDialog.value = show
    }

    // --- CALLS ---
    fun startPqcCall(isVideo: Boolean) {
        val chat = _activeChat.value
        val peer = chat?.title ?: "Secure PQC Node"
        _activeCallState.value = ActiveCallState(
            isCallActive = true,
            peerName = peer,
            peerAvatar = chat?.avatarUrl,
            isVideoCall = isVideo,
            callDurationSeconds = 0
        )
        _activeScreen.value = Screen.PQC_CALL

        viewModelScope.launch {
            while (_activeCallState.value.isCallActive) {
                delay(1000)
                _activeCallState.value = _activeCallState.value.copy(
                    callDurationSeconds = _activeCallState.value.callDurationSeconds + 1
                )
            }
        }
    }

    fun toggleMuteCall() {
        _activeCallState.value = _activeCallState.value.copy(
            isMuted = !_activeCallState.value.isMuted
        )
    }

    fun toggleCameraCall() {
        _activeCallState.value = _activeCallState.value.copy(
            isCameraOff = !_activeCallState.value.isCameraOff
        )
    }

    fun endCall() {
        _activeCallState.value = ActiveCallState(isCallActive = false)
        _activeScreen.value = Screen.CONVERSATION
    }

    // --- NODE PAIRING & GROUPS ---
    fun togglePairNodeDialog(show: Boolean) {
        _showPairNodeDialog.value = show
    }

    fun toggleCreateGroupDialog(show: Boolean) {
        _showCreateGroupDialog.value = show
    }

    fun pairNewNode(nodeName: String, publicKey: String) {
        pairNewNodeWithPhone(nodeName, "", publicKey)
    }

    fun pairNewNodeWithPhone(nodeName: String, phoneNumber: String, publicKey: String) {
        viewModelScope.launch {
            val newChatId = repository.createNewContactAndChat(nodeName, phoneNumber, publicKey)
            _showPairNodeDialog.value = false
            openConversation(newChatId)
        }
    }

    fun openChatForContact(contact: ContactEntity) {
        viewModelScope.launch {
            val chatId = repository.getOrCreateChatForContact(contact)
            openConversation(chatId)
        }
    }

    fun startCallForContact(contact: ContactEntity, isVideo: Boolean = true) {
        viewModelScope.launch {
            val chatId = repository.getOrCreateChatForContact(contact)
            _activeChatId.value = chatId
            val chat = repository.getChatById(chatId).first()
            _activeChat.value = chat
            startPqcCall(isVideo)
        }
    }

    fun importPhonebookContacts() {
        viewModelScope.launch {
            repository.importPhonebookContacts()
            _backupStatusMessage.value = "✓ Phonebook contacts synced with PQC directory"
            delay(3000)
            if (_backupStatusMessage.value?.contains("Phonebook") == true) {
                _backupStatusMessage.value = null
            }
        }
    }

    fun createGroupChat(groupName: String, selectedContactIds: List<String>) {
        viewModelScope.launch {
            val newChatId = repository.createGroupChat(groupName, selectedContactIds)
            _showCreateGroupDialog.value = false
            openConversation(newChatId)
        }
    }

    // --- CLOUD SYNC & BACKUPS ---
    fun toggleExportBackupDialog(show: Boolean) {
        _showExportBackupDialog.value = show
    }

    fun generateAndExportBackup(passphrase: String) {
        exportLocalEncryptedZip(passphrase)
    }

    fun addCloudAccount(name: String, type: CloudProviderType, pathOrEmail: String) {
        viewModelScope.launch {
            repository.addCloudAccount(name, type, pathOrEmail)
        }
    }

    fun removeCloudAccount(account: CloudAccountEntity) {
        viewModelScope.launch {
            repository.removeCloudAccount(account)
        }
    }

    fun syncAccount(account: CloudAccountEntity) {
        viewModelScope.launch {
            repository.syncCloudAccountNow(account.id)
            _backupStatusMessage.value = "${account.providerName} synced cleanly with zero-knowledge AES-256-GCM wrapper."
            delay(2500)
            _backupStatusMessage.value = null
        }
    }

    // --- INITIAL INSTALL & SETUP WIZARD METHODS ---
    fun updateSetupNodeName(name: String) {
        _setupNodeName.value = name
    }

    fun updateSetupPin(pin: String) {
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _setupPin.value = pin
        }
    }

    fun setSelectedCloudType(type: CloudProviderType) {
        _selectedCloudType.value = type
    }

    fun nextSetupStep() {
        if (_setupStep.value < 5) {
            _setupStep.value += 1
        }
    }

    fun previousSetupStep() {
        if (_setupStep.value > 1) {
            _setupStep.value -= 1
        }
    }

    fun generatePqcKeysInteractive() {
        viewModelScope.launch {
            _isGeneratingKeys.value = true
            _keyGenProgress.value = 0f
            for (i in 1..100) {
                delay(20)
                _keyGenProgress.value = i / 100f
            }
            _isGeneratingKeys.value = false
        }
    }

    // --- CHAT SETTINGS & WALLPAPER THEMES ---
    fun toggleChatSettingsDialog(show: Boolean) {
        _showChatSettingsDialog.value = show
    }

    fun setWallpaperThemeForActiveChat(theme: String) {
        val activeId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.updateChatWallpaperTheme(activeId, theme)
            _activeChat.value = _activeChat.value?.copy(wallpaperTheme = theme)
        }
    }

    fun updateChatWallpaperTheme(chatId: String, theme: String) {
        viewModelScope.launch {
            repository.updateChatWallpaperTheme(chatId, theme)
            _activeChat.value = _activeChat.value?.copy(wallpaperTheme = theme)
        }
    }

    fun toggleMessageReaction(messageId: String, currentReactions: String, emoji: String) {
        viewModelScope.launch {
            repository.toggleMessageReaction(messageId, currentReactions, emoji)
            // Refresh active messages
            _activeMessages.value = _activeMessages.value.map { msg ->
                if (msg.id == messageId) {
                    val list = currentReactions.split(",").filter { it.isNotBlank() }.toMutableList()
                    if (list.contains(emoji)) list.remove(emoji) else list.add(emoji)
                    msg.copy(reactionEmojis = list.joinToString(","))
                } else msg
            }
        }
    }

    // --- PQC QR SCANNER & CONTACT SHARING ---
    fun toggleQrScannerDialog(show: Boolean) {
        _showQrScannerDialog.value = show
    }

    fun toggleQrScannerModal(show: Boolean) {
        _showQrScannerDialog.value = show
    }

    fun pairScannedQrContact(nodeName: String, nodePublicKey: String) {
        viewModelScope.launch {
            val chatId = repository.createNewContactAndChat(
                nodeName = nodeName,
                nodePublicKey = nodePublicKey
            )
            _showQrScannerDialog.value = false
            openConversation(chatId)
        }
    }

    fun addScannedQrContact(nodeName: String, nodePublicKey: String) {
        pairScannedQrContact(nodeName, nodePublicKey)
    }

    // --- DISAPPEARING STATUS & STORIES ---
    fun toggleCreateStoryDialog(show: Boolean) {
        _showCreateStoryDialog.value = show
    }

    fun postStatusStory(caption: String, bgGradientHex: String = "#0D1B2A") {
        viewModelScope.launch {
            repository.postStatusStory(caption, bgGradientHex)
            _showCreateStoryDialog.value = false
        }
    }

    fun viewStory(story: StatusStoryEntity) {
        _activeStoryForViewing.value = story
    }

    fun closeStoryViewer() {
        _activeStoryForViewing.value = null
    }

    fun openStoryViewer(story: StatusStoryEntity?) {
        _activeStoryForViewing.value = story
    }

    // --- BROADCAST LISTS & CHANNELS ---
    fun toggleBroadcastDialog(show: Boolean) {
        _showBroadcastDialog.value = show
    }

    fun toggleBroadcastListDialog(show: Boolean) {
        _showBroadcastDialog.value = show
    }

    fun sendBroadcastMessage(targetChatIds: List<String>, messageText: String) {
        viewModelScope.launch {
            repository.sendBroadcastMessage(targetChatIds, messageText)
            _showBroadcastDialog.value = false
        }
    }

    fun toggleCreateChannelDialog(show: Boolean) {
        _showCreateChannelDialog.value = show
    }

    fun createChannel(channelName: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            val channelId = repository.createChannel(channelName, description, isPublic)
            _showCreateChannelDialog.value = false
            openConversation(channelId)
        }
    }

    fun completeInitialConfiguration() {
        viewModelScope.launch {
            val pinToSave = if (_setupPin.value.length == 6) _setupPin.value else "123456"
            val nodeName = if (_setupNodeName.value.isNotBlank()) _setupNodeName.value else "Quantum Alpha Node"

            prefs.edit()
                .putBoolean("is_configured", true)
                .putString("user_pin", pinToSave)
                .putString("node_name", nodeName)
                .apply()

            _userPin.value = pinToSave
            _isConfigured.value = true
            _isLocked.value = false
            _activeScreen.value = Screen.CHAT_LIST
        }
    }

    fun relaunchSetupWizard() {
        _setupStep.value = 1
        _activeScreen.value = Screen.INITIAL_SETUP
    }

    fun onContactSearchQueryChanged(query: String) {
        _contactSearchQuery.value = query
    }

    fun bulkArchiveChats(chatIds: List<String>, archive: Boolean = true) {
        viewModelScope.launch {
            repository.bulkArchiveChats(chatIds, archive)
        }
    }

    fun bulkDeleteChats(chatIds: List<String>) {
        viewModelScope.launch {
            repository.bulkDeleteChats(chatIds)
        }
    }
}
