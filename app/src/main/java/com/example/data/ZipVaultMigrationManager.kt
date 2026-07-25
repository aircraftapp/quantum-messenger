package com.example.data

import android.content.Context
import android.util.Base64
import com.example.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class MigrationExportResult(
    val file: File,
    val fileSizeFormatted: String,
    val messagesCount: Int,
    val chatsCount: Int,
    val contactsCount: Int,
    val timestamp: Long
)

data class MigrationImportResult(
    val success: Boolean,
    val message: String,
    val restoredMessagesCount: Int,
    val restoredChatsCount: Int,
    val restoredContactsCount: Int
)

object ZipVaultMigrationManager {

    private const val PBKDF2_ITERATIONS = 10000
    private const val KEY_LENGTH_BITS = 256
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val SALT_LENGTH = 16

    /**
     * Exports all user data into a password-protected, AES-256-GCM encrypted ZIP archive file.
     */
    suspend fun exportEncryptedZipVault(
        context: Context,
        database: QuantumDatabase,
        password: String
    ): MigrationExportResult = withContext(Dispatchers.IO) {
        val dao = database.quantumDao()

        val chats = dao.getAllChatsDirect()
        val messages = dao.getAllMessagesDirect()
        val contacts = dao.getAllContactsDirect()
        val cloudAccounts = dao.getAllCloudAccountsDirect()

        // 1. Convert entities to JSON
        val chatsJson = JSONArray()
        chats.forEach { chat ->
            chatsJson.put(JSONObject().apply {
                put("id", chat.id)
                put("title", chat.title)
                put("isGroup", chat.isGroup)
                put("participantIdsCsv", chat.participantIdsCsv)
                put("lastMessage", chat.lastMessage)
                put("lastMessageTime", chat.lastMessageTime)
                put("unreadCount", chat.unreadCount)
                put("securityFingerprint", chat.securityFingerprint)
            })
        }

        val messagesJson = JSONArray()
        messages.forEach { msg ->
            messagesJson.put(JSONObject().apply {
                put("id", msg.id)
                put("chatId", msg.chatId)
                put("senderId", msg.senderId)
                put("senderName", msg.senderName)
                put("isFromMe", msg.isFromMe)
                put("textContent", msg.textContent)
                put("encryptedPayload", msg.encryptedPayload)
                put("messageType", msg.messageType.name)
                put("timestamp", msg.timestamp)
                put("ephemeralDurationSeconds", msg.ephemeralDurationSeconds)
                put("mediaUri", msg.mediaUri)
            })
        }

        val contactsJson = JSONArray()
        contacts.forEach { contact ->
            contactsJson.put(JSONObject().apply {
                put("id", contact.id)
                put("name", contact.name)
                put("phoneNumber", contact.phoneNumber)
                put("pqcPublicKey", contact.pqcPublicKey)
                put("verifiedFingerprint", contact.verifiedFingerprint)
                put("isOnline", contact.isOnline)
            })
        }

        val accountsJson = JSONArray()
        cloudAccounts.forEach { acc ->
            accountsJson.put(JSONObject().apply {
                put("id", acc.id)
                put("providerName", acc.providerName)
                put("providerType", acc.providerType.name)
                put("accountEmailOrPath", acc.accountEmailOrPath)
                put("lastSyncTimestamp", acc.lastSyncTimestamp)
            })
        }

        val manifestJson = JSONObject().apply {
            put("appVersion", "1.0.0-QCRYPT")
            put("timestamp", System.currentTimeMillis())
            put("chatsCount", chats.size)
            put("messagesCount", messages.size)
            put("contactsCount", contacts.size)
            put("encryption", "PBKDF2-AES256-GCM")
        }

        // 2. Build In-Memory Raw Zip Stream
        val zipByteStream = ByteArrayOutputStream()
        ZipOutputStream(zipByteStream).use { zos ->
            zos.putNextEntry(ZipEntry("manifest.json"))
            zos.write(manifestJson.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("chats.json"))
            zos.write(chatsJson.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("messages.json"))
            zos.write(messagesJson.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("contacts.json"))
            zos.write(contactsJson.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            zos.putNextEntry(ZipEntry("cloud_accounts.json"))
            zos.write(accountsJson.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()
        }

        val rawZipBytes = zipByteStream.toByteArray()

        // 3. Derive 256-bit Key from Password using PBKDF2
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        val keySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")

        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val encryptedZipBytes = cipher.doFinal(rawZipBytes)

        // 4. Construct Final Protected File with Metadata Header
        // Structure: "QVAULT_ZIP_V1\n" + Salt Base64 + "\n" + IV Base64 + "\n" + Encrypted Content
        val finalOutStream = ByteArrayOutputStream()
        finalOutStream.write("QVAULT_ZIP_V1\n".toByteArray(Charsets.UTF_8))
        finalOutStream.write("${Base64.encodeToString(salt, Base64.NO_WRAP)}\n".toByteArray(Charsets.UTF_8))
        finalOutStream.write("${Base64.encodeToString(iv, Base64.NO_WRAP)}\n".toByteArray(Charsets.UTF_8))
        finalOutStream.write(encryptedZipBytes)

        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timestamp = System.currentTimeMillis()
        val file = File(backupDir, "quantum_vault_backup_$timestamp.zip")
        file.writeBytes(finalOutStream.toByteArray())

        val sizeKb = file.length() / 1024
        val sizeFormatted = if (sizeKb > 1024) "${"%.2f".format(sizeKb / 1024f)} MB" else "$sizeKb KB"

        MigrationExportResult(
            file = file,
            fileSizeFormatted = sizeFormatted,
            messagesCount = messages.size,
            chatsCount = chats.size,
            contactsCount = contacts.size,
            timestamp = timestamp
        )
    }

    /**
     * Decrypts and imports a password-protected encrypted ZIP vault archive into the local Room database.
     */
    suspend fun importEncryptedZipVault(
        context: Context,
        database: QuantumDatabase,
        zipFile: File,
        password: String
    ): MigrationImportResult = withContext(Dispatchers.IO) {
        val dao = database.quantumDao()

        try {
            val fileBytes = zipFile.readBytes()
            val textStream = String(fileBytes.take(256).toByteArray(), Charsets.UTF_8)
            val lines = textStream.split("\n")

            if (lines.size < 3 || lines[0].trim() != "QVAULT_ZIP_V1") {
                return@withContext MigrationImportResult(
                    success = false,
                    message = "Invalid backup archive header format.",
                    0, 0, 0
                )
            }

            val saltBase64 = lines[1].trim()
            val ivBase64 = lines[2].trim()

            val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)

            // Header line length offset calculation
            val headerOffset = lines[0].length + lines[1].length + lines[2].length + 3
            val encryptedContent = fileBytes.copyOfRange(headerOffset, fileBytes.size)

            // Derive key from password
            val keySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
            val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedZipBytes = cipher.doFinal(encryptedContent)

            // Read ZIP archive contents
            var chatsCount = 0
            var messagesCount = 0
            var contactsCount = 0

            ZipInputStream(ByteArrayInputStream(decryptedZipBytes)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    val jsonStr = zis.readBytes().toString(Charsets.UTF_8)

                    when (entryName) {
                        "contacts.json" -> {
                            val array = JSONArray(jsonStr)
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                dao.insertContact(
                                    ContactEntity(
                                        id = obj.getString("id"),
                                        name = obj.getString("name"),
                                        phoneNumber = obj.optString("phoneNumber", ""),
                                        pqcPublicKey = obj.getString("pqcPublicKey"),
                                        verifiedFingerprint = obj.getString("verifiedFingerprint"),
                                        isOnline = obj.optBoolean("isOnline", true)
                                    )
                                )
                                contactsCount++
                            }
                        }
                        "chats.json" -> {
                            val array = JSONArray(jsonStr)
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                dao.insertChat(
                                    ChatEntity(
                                        id = obj.getString("id"),
                                        title = obj.getString("title"),
                                        isGroup = obj.getBoolean("isGroup"),
                                        participantIdsCsv = obj.getString("participantIdsCsv"),
                                        lastMessage = obj.getString("lastMessage"),
                                        lastMessageTime = obj.getLong("lastMessageTime"),
                                        unreadCount = obj.optInt("unreadCount", 0),
                                        securityFingerprint = obj.getString("securityFingerprint")
                                    )
                                )
                                chatsCount++
                            }
                        }
                        "messages.json" -> {
                            val array = JSONArray(jsonStr)
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                val msgTypeStr = obj.optString("messageType", "TEXT")
                                val msgType = try { MessageType.valueOf(msgTypeStr) } catch (e: Exception) { MessageType.TEXT }

                                dao.insertMessage(
                                    MessageEntity(
                                        id = obj.getString("id"),
                                        chatId = obj.getString("chatId"),
                                        senderId = obj.getString("senderId"),
                                        senderName = obj.optString("senderName", "Node"),
                                        isFromMe = obj.optBoolean("isFromMe", false),
                                        textContent = obj.optString("textContent", obj.optString("content", "")),
                                        encryptedPayload = obj.optString("encryptedPayload", "PQC_CIPHERTEXT"),
                                        messageType = msgType,
                                        mediaUri = obj.optString("mediaUri", null),
                                        timestamp = obj.getLong("timestamp"),
                                        ephemeralDurationSeconds = obj.optLong("ephemeralDurationSeconds", 0L)
                                    )
                                )
                                messagesCount++
                            }
                        }
                        "cloud_accounts.json" -> {
                            val array = JSONArray(jsonStr)
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                val providerTypeStr = obj.optString("providerType", "GOOGLE_DRIVE")
                                val providerType = try {
                                    CloudProviderType.valueOf(providerTypeStr)
                                } catch (e: Exception) {
                                    CloudProviderType.GOOGLE_DRIVE
                                }

                                dao.insertCloudAccount(
                                    CloudAccountEntity(
                                        id = obj.getString("id"),
                                        providerName = obj.getString("providerName"),
                                        providerType = providerType,
                                        accountEmailOrPath = obj.getString("accountEmailOrPath"),
                                        lastSyncTimestamp = obj.getLong("lastSyncTimestamp")
                                    )
                                )
                            }
                        }
                    }

                    entry = zis.nextEntry
                }
            }

            MigrationImportResult(
                success = true,
                message = "Successfully restored $messagesCount messages, $chatsCount chats, and $contactsCount contacts.",
                restoredMessagesCount = messagesCount,
                restoredChatsCount = chatsCount,
                restoredContactsCount = contactsCount
            )
        } catch (e: Exception) {
            MigrationImportResult(
                success = false,
                message = "Restore failed: Invalid password or corrupted archive (${e.localizedMessage})",
                0, 0, 0
            )
        }
    }

    fun getLocalBackupFiles(context: Context): List<File> {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) return emptyList()
        return backupDir.listFiles { _, name -> name.endsWith(".zip") || name.endsWith(".qpkg") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
}
