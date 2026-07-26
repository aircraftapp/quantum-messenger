package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.QuantumViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.QuantumMessengerTheme

import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuantumMessengerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuantumMessengerApp()
                }
            }
        }
    }
}

@Composable
fun QuantumMessengerApp(
    viewModel: QuantumViewModel = viewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                viewModel.saveCurrentDraftToRoomDb()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val pinInput by viewModel.pinInput.collectAsStateWithLifecycle()
    val pinError by viewModel.pinError.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()

    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val chats by viewModel.allChats.collectAsStateWithLifecycle()
    val contacts by viewModel.allContacts.collectAsStateWithLifecycle()
    val searchedContacts by viewModel.searchedContacts.collectAsStateWithLifecycle()
    val contactSearchQuery by viewModel.contactSearchQuery.collectAsStateWithLifecycle()
    val cloudAccounts by viewModel.allCloudAccounts.collectAsStateWithLifecycle()

    val activeChat by viewModel.activeChat.collectAsStateWithLifecycle()
    val messages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val messageInput by viewModel.messageInput.collectAsStateWithLifecycle()

    val selectedEphemeralSeconds by viewModel.selectedEphemeralSeconds.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val recordingDurationSeconds by viewModel.recordingDurationSeconds.collectAsStateWithLifecycle()

    val activeCallState by viewModel.activeCallState.collectAsStateWithLifecycle()
    val computeMetrics by viewModel.computeMetrics.collectAsStateWithLifecycle()

    val p2pServerStatus by viewModel.p2pServerStatus.collectAsStateWithLifecycle()
    val p2pServerInfo by viewModel.p2pServerInfo.collectAsStateWithLifecycle()
    val p2pActivePeers by viewModel.p2pActivePeers.collectAsStateWithLifecycle()

    val showPairNodeDialog by viewModel.showPairNodeDialog.collectAsStateWithLifecycle()
    val showCreateGroupDialog by viewModel.showCreateGroupDialog.collectAsStateWithLifecycle()
    val showEphemeralDialog by viewModel.showEphemeralDialog.collectAsStateWithLifecycle()
    val showExportBackupDialog by viewModel.showExportBackupDialog.collectAsStateWithLifecycle()
    val showImportBackupDialog by viewModel.showImportBackupDialog.collectAsStateWithLifecycle()

    val showChatSettingsDialog by viewModel.showChatSettingsDialog.collectAsStateWithLifecycle()
    val showQrScannerModal by viewModel.showQrScannerModal.collectAsStateWithLifecycle()
    val isReadReceiptsEnabled by viewModel.isReadReceiptsEnabled.collectAsStateWithLifecycle()
    val groupNotificationSounds by viewModel.groupNotificationSounds.collectAsStateWithLifecycle()

    val isTorRoutingEnabled by viewModel.isTorRoutingEnabled.collectAsStateWithLifecycle()
    val torCircuitStatus by viewModel.torCircuitStatus.collectAsStateWithLifecycle()
    val torOnionAddress by viewModel.torOnionAddress.collectAsStateWithLifecycle()

    val isEnterpriseModeEnabled by viewModel.isEnterpriseModeEnabled.collectAsStateWithLifecycle()
    val isScreenshotPreventionEnabled by viewModel.isScreenshotPreventionEnabled.collectAsStateWithLifecycle()
    val isClipboardIsolationEnabled by viewModel.isClipboardIsolationEnabled.collectAsStateWithLifecycle()
    val isRemoteWipeConfigured by viewModel.isRemoteWipeConfigured.collectAsStateWithLifecycle()
    val deadManSwitchDays by viewModel.deadManSwitchDays.collectAsStateWithLifecycle()
    val isSystemRootedOrCompromised by viewModel.isSystemRootedOrCompromised.collectAsStateWithLifecycle()
    val hardwareKeystoreAttested by viewModel.hardwareKeystoreAttested.collectAsStateWithLifecycle()
    val hardwareKeystoreType by viewModel.hardwareKeystoreType.collectAsStateWithLifecycle()

    val isExportingApk by viewModel.isExportingApk.collectAsStateWithLifecycle()
    val exportedApkChecksum by viewModel.exportedApkChecksum.collectAsStateWithLifecycle()
    val showCreateStoryDialog by viewModel.showCreateStoryDialog.collectAsStateWithLifecycle()
    val showStoryViewerModal by viewModel.showStoryViewerModal.collectAsStateWithLifecycle()
    val showBroadcastListDialog by viewModel.showBroadcastListDialog.collectAsStateWithLifecycle()
    val showCreateChannelDialog by viewModel.showCreateChannelDialog.collectAsStateWithLifecycle()

    val statusStories by viewModel.statusStories.collectAsStateWithLifecycle()
    val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()

    val localBackupFiles by viewModel.localBackupFiles.collectAsStateWithLifecycle()
    val activeMediaJobs by viewModel.activeMediaJobs.collectAsStateWithLifecycle()
    val backupStatusMessage by viewModel.backupStatusMessage.collectAsStateWithLifecycle()

    val isWalkieTalkieModeActive by viewModel.isWalkieTalkieModeActive.collectAsStateWithLifecycle()
    val selectedPttChannel by viewModel.selectedPttChannel.collectAsStateWithLifecycle()
    val isPttTransmitting by viewModel.isPttTransmitting.collectAsStateWithLifecycle()
    val pttTransmissionDuration by viewModel.pttTransmissionDuration.collectAsStateWithLifecycle()
    val latestPttReceivedAlert by viewModel.latestPttReceivedAlert.collectAsStateWithLifecycle()

    val sharedFolders by viewModel.sharedFolders.collectAsStateWithLifecycle()
    val activeFileOperationName by viewModel.activeFileOperationName.collectAsStateWithLifecycle()
    val fileOperationProgress by viewModel.fileOperationProgress.collectAsStateWithLifecycle()
    val fileOperationStatus by viewModel.fileOperationStatus.collectAsStateWithLifecycle()

    val diagnosticMetrics by viewModel.diagnosticMetrics.collectAsStateWithLifecycle()
    val isDiagnosticPanelOpen by viewModel.isDiagnosticPanelOpen.collectAsStateWithLifecycle()

    val p2pDataUsageMetrics by viewModel.p2pDataUsageMetrics.collectAsStateWithLifecycle()
    val globalDefaultEphemeralSeconds by viewModel.globalDefaultEphemeralSeconds.collectAsStateWithLifecycle()

    val chatDrafts by viewModel.chatDrafts.collectAsStateWithLifecycle()
    val selectedContactTag by viewModel.selectedContactTag.collectAsStateWithLifecycle()
    val isChatSearchActive by viewModel.isChatSearchActive.collectAsStateWithLifecycle()
    val inChatSearchQuery by viewModel.inChatSearchQuery.collectAsStateWithLifecycle()

    val setupStep by viewModel.setupStep.collectAsStateWithLifecycle()
    val setupNodeName by viewModel.setupNodeName.collectAsStateWithLifecycle()
    val setupPin by viewModel.setupPin.collectAsStateWithLifecycle()
    val selectedCloudType by viewModel.selectedCloudType.collectAsStateWithLifecycle()
    val keyGenProgress by viewModel.keyGenProgress.collectAsStateWithLifecycle()
    val isGeneratingKeys by viewModel.isGeneratingKeys.collectAsStateWithLifecycle()

    // Handle Android hardware back press when in sub-screens
    if (activeScreen != Screen.CHAT_LIST && activeScreen != Screen.INITIAL_SETUP && !isLocked) {
        BackHandler {
            if (activeCallState.isCallActive) {
                viewModel.endCall()
            } else {
                viewModel.navigateToScreen(Screen.CHAT_LIST)
            }
        }
    }

    if (activeScreen == Screen.INITIAL_SETUP) {
        InitialSetupScreen(
            currentStep = setupStep,
            nodeName = setupNodeName,
            setupPin = setupPin,
            selectedCloudType = selectedCloudType,
            keyGenProgress = keyGenProgress,
            isGeneratingKeys = isGeneratingKeys,
            isBiometricEnabled = isBiometricEnabled,
            onNodeNameChange = { viewModel.updateSetupNodeName(it) },
            onPinChange = { viewModel.updateSetupPin(it) },
            onBiometricToggle = { viewModel.toggleBiometricEnabled(it) },
            onCloudTypeSelect = { viewModel.setSelectedCloudType(it) },
            onGenerateKeysClick = { viewModel.generatePqcKeysInteractive() },
            onNextStepClick = { viewModel.nextSetupStep() },
            onPreviousStepClick = { viewModel.previousSetupStep() },
            onCompleteSetupClick = { viewModel.completeInitialConfiguration() }
        )
    } else if (isLocked) {
        BiometricLockScreen(
            pinInput = pinInput,
            pinError = pinError,
            isBiometricEnabled = isBiometricEnabled,
            onDigitClick = { viewModel.appendPinDigit(it) },
            onBackspaceClick = { viewModel.backspacePinDigit() },
            onBiometricAuthSuccess = { viewModel.authenticateBiometricSuccess() },
            onToggleBiometricOption = { viewModel.toggleBiometricEnabled(it) }
        )
    } else {
        when (activeScreen) {
            Screen.INITIAL_SETUP -> { /* Handled above */ }
            Screen.CHAT_LIST -> {
                ChatListScreen(
                    chats = chats,
                    contacts = contacts,
                    chatDrafts = chatDrafts,
                    statusStories = statusStories,
                    onChatClick = { chatId -> viewModel.openConversation(chatId) },
                    onNavigate = { screen -> viewModel.navigateToScreen(screen) },
                    onPairNodeClick = { viewModel.togglePairNodeDialog(true) },
                    onCreateGroupClick = { viewModel.toggleCreateGroupDialog(true) },
                    onLockVaultClick = { viewModel.lockApp() },
                    onReopenSetupClick = { viewModel.relaunchSetupWizard() },
                    onContactsClick = { viewModel.navigateToScreen(Screen.CONTACTS_DIRECTORY) },
                    onOpenQrScanner = { viewModel.toggleQrScannerModal(true) },
                    onOpenCreateStory = { viewModel.toggleCreateStoryDialog(true) },
                    onViewStory = { story -> viewModel.viewStory(story) },
                    onOpenBroadcast = { viewModel.toggleBroadcastListDialog(true) },
                    onOpenCreateChannel = { viewModel.toggleCreateChannelDialog(true) },
                    onBulkArchive = { ids, archive -> viewModel.bulkArchiveChats(ids, archive) },
                    onBulkDelete = { ids -> viewModel.bulkDeleteChats(ids) }
                )
            }

            Screen.CONTACTS_DIRECTORY -> {
                ContactsDirectoryScreen(
                    contacts = searchedContacts,
                    searchQueryParam = contactSearchQuery,
                    selectedTag = selectedContactTag,
                    onSearchQueryChange = { query -> viewModel.onContactSearchQueryChanged(query) },
                    onSelectTag = { tag -> viewModel.selectContactTag(tag) },
                    onUpdateContactTag = { contactId, newTag -> viewModel.updateContactTag(contactId, newTag) },
                    onBackClick = { viewModel.navigateToScreen(Screen.CHAT_LIST) },
                    onContactSelect = { contact -> viewModel.openChatForContact(contact) },
                    onAddContactSubmit = { name, phone, key -> viewModel.pairNewNodeWithPhone(name, phone, key) },
                    onSyncPhonebookClick = { viewModel.importPhonebookContacts() },
                    onCreateGroupClick = { viewModel.toggleCreateGroupDialog(true) },
                    onCallContactClick = { contact, isVideo -> viewModel.startCallForContact(contact, isVideo) },
                    onOpenQrScanner = { viewModel.toggleQrScannerModal(true) },
                    onToggleBlockContact = { contactId, isBlocked -> viewModel.toggleContactBlockStatus(contactId, isBlocked) }
                )
            }

            Screen.CONVERSATION -> {
                val activeContact = contacts.find { it.id == activeChat?.participantIdsCsv || it.name.equals(activeChat?.title, ignoreCase = true) }
                val isContactBlocked = activeContact?.isBlocked ?: false

                ConversationScreen(
                    chat = activeChat,
                    messages = messages,
                    messageInput = messageInput,
                    selectedEphemeralSeconds = selectedEphemeralSeconds,
                    isRecordingAudio = isRecordingAudio,
                    recordingDurationSeconds = recordingDurationSeconds,
                    isWalkieTalkieModeActive = isWalkieTalkieModeActive,
                    selectedPttChannel = selectedPttChannel,
                    isPttTransmitting = isPttTransmitting,
                    pttTransmissionDuration = pttTransmissionDuration,
                    latestPttReceivedAlert = latestPttReceivedAlert,
                    activeFileOperationName = activeFileOperationName,
                    fileOperationProgress = fileOperationProgress,
                    fileOperationStatus = fileOperationStatus,
                    diagnosticMetrics = diagnosticMetrics,
                    isDiagnosticPanelOpen = isDiagnosticPanelOpen,
                    isChatSearchActive = isChatSearchActive,
                    inChatSearchQuery = inChatSearchQuery,
                    onBackClick = { viewModel.navigateToScreen(Screen.CHAT_LIST) },
                    onMessageInputChange = { text -> viewModel.updateMessageInput(text) },
                    onSendMessageClick = { viewModel.sendTextMessage() },
                    onToggleAudioRecording = { viewModel.toggleAudioRecording() },
                    onSendVideoNoteClick = { viewModel.sendVideoNote() },
                    onSendFileClick = { fileName, fileSize -> viewModel.sendEncryptedFile(fileName, fileSize) },
                    onDecryptFileClick = { msgId, fName -> viewModel.decryptAndDownloadFileLocally(msgId, fName) },
                    onEphemeralTimerClick = { viewModel.toggleEphemeralDialog(true) },
                    onOpenSettingsClick = { viewModel.toggleChatSettingsDialog(true) },
                    onToggleReaction = { msgId, curReactions, emoji -> viewModel.toggleMessageReaction(msgId, curReactions, emoji) },
                    onStartCallClick = { isVideo -> viewModel.startPqcCall(isVideo) },
                    onToggleWalkieTalkieMode = { viewModel.toggleWalkieTalkieMode() },
                    onSelectPttChannel = { ch -> viewModel.selectPttChannel(ch) },
                    onStartPttTransmission = { viewModel.startPttTransmission() },
                    onStopPttTransmission = { viewModel.stopPttTransmissionAndSend() },
                    onSendPttQuickBurst = { burst -> viewModel.sendPttVoiceBurst(viewModel.activeChatId.value ?: "", selectedPttChannel, 2, burst) },
                    onDismissPttAlert = { viewModel.dismissPttAlert() },
                    onToggleDiagnosticPanel = { viewModel.toggleDiagnosticPanel() },
                    onToggleChatSearch = { active -> viewModel.toggleChatSearch(active) },
                    onUpdateInChatSearchQuery = { query -> viewModel.updateInChatSearchQuery(query) },
                    onExportChatHistoryClick = { passphrase -> viewModel.exportChatHistory(activeChat?.id ?: "", passphrase) },
                    isContactBlocked = isContactBlocked,
                    onToggleBlockContact = { isBlocked -> activeContact?.let { viewModel.toggleContactBlockStatus(it.id, isBlocked) } }
                )
            }

            Screen.PQC_CALL -> {
                PqcCallScreen(
                    callState = activeCallState,
                    computeMetrics = computeMetrics,
                    onToggleMute = { viewModel.toggleMuteCall() },
                    onToggleCamera = { viewModel.toggleCameraCall() },
                    onEndCall = { viewModel.endCall() }
                )
            }

            Screen.CLOUD_SYNC -> {
                CloudSyncDashboardScreen(
                    cloudAccounts = cloudAccounts,
                    localBackupFiles = localBackupFiles,
                    statusMessage = backupStatusMessage,
                    sharedFolders = sharedFolders,
                    onBackClick = { viewModel.navigateToScreen(Screen.CHAT_LIST) },
                    onSyncAccountClick = { account -> viewModel.syncAccount(account) },
                    onDeleteAccountClick = { account -> viewModel.removeCloudAccount(account) },
                    onExportZipClick = { viewModel.toggleExportBackupDialog(true) },
                    onImportZipClick = { viewModel.toggleImportBackupDialog(true) },
                    onAddCloudAccountClick = { name, type, path -> viewModel.addCloudAccount(name, type, path) },
                    onCreateSharedFolder = { folderName, peer -> viewModel.createSharedFolder(folderName, peer) },
                    onSyncSharedFolder = { folderId -> viewModel.syncSharedFolderNow(folderId) }
                )
            }

            Screen.COMPUTE_DASHBOARD -> {
                LocalComputeDashboardScreen(
                    computeMetrics = computeMetrics,
                    activeMediaJobs = activeMediaJobs,
                    p2pServerStatus = p2pServerStatus,
                    p2pServerInfo = p2pServerInfo,
                    p2pActivePeers = p2pActivePeers,
                    dataUsageMetrics = p2pDataUsageMetrics,
                    globalEphemeralTtl = globalDefaultEphemeralSeconds,
                    onToggleP2pServer = { enable -> viewModel.toggleP2pServer(enable) },
                    onConnectToP2pPeer = { peerAddr -> viewModel.connectToP2pPeer(peerAddr) },
                    onEnqueueTestJob = { type -> viewModel.enqueueMediaTranscodeAndEncryption("sample_media_uri", type) },
                    onToggleBatterySaver = { enable -> viewModel.toggleBatterySaverMode(enable) },
                    onResetDataUsage = { viewModel.resetDataUsageStats() },
                    onSetGlobalEphemeralTtl = { seconds -> viewModel.setGlobalDefaultEphemeralTtl(seconds) },
                    onBackClick = { viewModel.navigateToScreen(Screen.CHAT_LIST) }
                )
            }

            Screen.LANDING_PAGE -> {
                LandingPageScreen(
                    isTorEnabled = isTorRoutingEnabled,
                    torCircuitStatus = torCircuitStatus,
                    torOnionAddress = torOnionAddress,
                    isEnterpriseMode = isEnterpriseModeEnabled,
                    isExportingApk = isExportingApk,
                    exportedApkChecksum = exportedApkChecksum,
                    onLaunchApp = { viewModel.navigateToScreen(Screen.CHAT_LIST) },
                    onOpenEnterpriseMdm = { viewModel.navigateToScreen(Screen.ENTERPRISE_MDM) },
                    onOpenComputeDashboard = { viewModel.navigateToScreen(Screen.COMPUTE_DASHBOARD) },
                    onToggleTorRouting = { enable -> viewModel.toggleTorRouting(enable) },
                    onExportApkBundle = { viewModel.exportApkBundle() }
                )
            }

            Screen.ENTERPRISE_MDM -> {
                EnterpriseMdmScreen(
                    isEnterpriseMode = isEnterpriseModeEnabled,
                    isTorEnabled = isTorRoutingEnabled,
                    torCircuitStatus = torCircuitStatus,
                    torOnionAddress = torOnionAddress,
                    isScreenshotPreventionEnabled = isScreenshotPreventionEnabled,
                    isClipboardIsolationEnabled = isClipboardIsolationEnabled,
                    isRemoteWipeConfigured = isRemoteWipeConfigured,
                    deadManSwitchDays = deadManSwitchDays,
                    isSystemRootedOrCompromised = isSystemRootedOrCompromised,
                    hardwareKeystoreAttested = hardwareKeystoreAttested,
                    hardwareKeystoreType = hardwareKeystoreType,
                    statusMessage = backupStatusMessage ?: "",
                    onBackClick = { viewModel.navigateToScreen(Screen.CHAT_LIST) },
                    onToggleEnterpriseMode = { enable -> viewModel.toggleEnterpriseMode(enable) },
                    onToggleTorRouting = { enable -> viewModel.toggleTorRouting(enable) },
                    onToggleScreenshotPrevention = { enable -> viewModel.toggleScreenshotPrevention(enable) },
                    onToggleClipboardIsolation = { enable -> viewModel.toggleClipboardIsolation(enable) },
                    onUpdateDeadManSwitchDays = { days -> viewModel.updateDeadManSwitchDays(days) },
                    onToggleSystemIntegritySimulation = { compromised -> viewModel.toggleSystemIntegritySimulation(compromised) },
                    onTriggerRemoteWipe = { viewModel.triggerSimulatedRemoteWipe() }
                )
            }
        }

        // Dialog Overlays
        if (showPairNodeDialog) {
            PairNodeDialog(
                onDismiss = { viewModel.togglePairNodeDialog(false) },
                onPairNode = { name, pubKey -> viewModel.pairNewNode(name, pubKey) }
            )
        }

        if (showCreateGroupDialog) {
            CreateGroupDialog(
                contacts = contacts,
                onDismiss = { viewModel.toggleCreateGroupDialog(false) },
                onCreateGroup = { name, selectedIds -> viewModel.createGroupChat(name, selectedIds) }
            )
        }

        if (showEphemeralDialog) {
            EphemeralTimerDialog(
                currentSeconds = selectedEphemeralSeconds,
                onDismiss = { viewModel.toggleEphemeralDialog(false) },
                onSelectSeconds = { seconds -> viewModel.setEphemeralTimerForActiveChat(seconds) }
            )
        }

        if (showExportBackupDialog) {
            ExportBackupDialog(
                onDismiss = { viewModel.toggleExportBackupDialog(false) },
                onGenerateBackup = { passphrase -> viewModel.generateAndExportBackup(passphrase) }
            )
        }

        if (showImportBackupDialog) {
            ImportBackupDialog(
                backupFiles = localBackupFiles,
                onDismiss = { viewModel.toggleImportBackupDialog(false) },
                onImportBackup = { file, password -> viewModel.importLocalEncryptedZip(file, password) }
            )
        }

        if (showChatSettingsDialog && activeChat != null) {
            val activeContact = contacts.find { it.id == activeChat?.participantIdsCsv || it.name.equals(activeChat?.title, ignoreCase = true) }
            val isContactBlocked = activeContact?.isBlocked ?: false

            ChatSettingsDialog(
                chat = activeChat!!,
                isContactBlocked = isContactBlocked,
                isReadReceiptsEnabled = isReadReceiptsEnabled,
                groupNotificationSounds = groupNotificationSounds,
                onDismiss = { viewModel.toggleChatSettingsDialog(false) },
                onEphemeralTimerChange = { seconds -> viewModel.setEphemeralTimerForActiveChat(seconds) },
                onWallpaperThemeChange = { theme -> viewModel.setWallpaperThemeForActiveChat(theme) },
                onExportChatHistory = { passphrase -> viewModel.exportChatHistory(activeChat?.id ?: "", passphrase) },
                onToggleBlockContact = { isBlocked -> activeContact?.let { viewModel.toggleContactBlockStatus(it.id, isBlocked) } },
                onToggleReadReceipts = { enabled -> viewModel.toggleReadReceipts(enabled) },
                onUpdateGroupNotificationSound = { groupName, soundName -> viewModel.updateGroupNotificationSound(groupName, soundName) }
            )
        }

        if (showQrScannerModal) {
            QrScannerModal(
                userNodeName = setupNodeName.ifBlank { "Quantum Node Alpha" },
                onDismiss = { viewModel.toggleQrScannerModal(false) },
                onContactScanned = { name, pubKey -> viewModel.pairScannedQrContact(name, pubKey) }
            )
        }

        if (showCreateStoryDialog) {
            CreateStoryDialog(
                onDismiss = { viewModel.toggleCreateStoryDialog(false) },
                onPostStory = { caption, bgHex -> viewModel.postStatusStory(caption, bgHex) }
            )
        }

        if (showStoryViewerModal && activeStory != null) {
            StoryViewerModal(
                story = activeStory!!,
                onDismiss = { viewModel.closeStoryViewer() }
            )
        }

        if (showBroadcastListDialog) {
            BroadcastListDialog(
                chats = chats,
                onDismiss = { viewModel.toggleBroadcastListDialog(false) },
                onSendBroadcast = { selectedIds, msg -> viewModel.sendBroadcastMessage(selectedIds, msg) }
            )
        }

        if (showCreateChannelDialog) {
            CreateChannelDialog(
                onDismiss = { viewModel.toggleCreateChannelDialog(false) },
                onCreateChannel = { name, desc, isPub -> viewModel.createChannel(name, desc, isPub) }
            )
        }
    }
}
