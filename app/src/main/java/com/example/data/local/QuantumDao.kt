package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuantumDao {

    // --- CHATS ---
    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    suspend fun getAllChatsDirect(): List<ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChatById(chatId: String): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)

    @Query("UPDATE chats SET isArchived = :isArchived WHERE id IN (:chatIds)")
    suspend fun updateChatsArchivedStatus(chatIds: List<String>, isArchived: Boolean)

    @Query("DELETE FROM chats WHERE id IN (:chatIds)")
    suspend fun deleteChats(chatIds: List<String>)

    @Query("DELETE FROM messages WHERE chatId IN (:chatIds)")
    suspend fun deleteMessagesForChats(chatIds: List<String>)

    @Query("UPDATE chats SET ephemeralSettingSeconds = :ephemeralSeconds WHERE id = :chatId")
    suspend fun updateChatEphemeralTimer(chatId: String, ephemeralSeconds: Long)

    @Query("UPDATE chats SET wallpaperTheme = :theme WHERE id = :chatId")
    suspend fun updateChatWallpaperTheme(chatId: String, theme: String)

    @Query("UPDATE chats SET draftText = :draftText WHERE id = :chatId")
    suspend fun updateChatDraft(chatId: String, draftText: String)

    // --- MESSAGES ---
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesDirect(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET reactionEmojis = :reactions WHERE id = :messageId")
    suspend fun updateMessageReactions(messageId: String, reactions: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages WHERE expiresAtTimestamp > 0 AND expiresAtTimestamp <= :nowTimestamp")
    suspend fun purgeExpiredEphemeralMessages(nowTimestamp: Long)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteAllMessagesInChat(chatId: String)

    // --- STATUS STORIES ---
    @Query("SELECT * FROM status_stories WHERE expiresAtTimestamp > :nowTimestamp ORDER BY timestamp DESC")
    fun getActiveStatusStories(nowTimestamp: Long = System.currentTimeMillis()): Flow<List<StatusStoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusStory(story: StatusStoryEntity)

    @Query("DELETE FROM status_stories WHERE expiresAtTimestamp <= :nowTimestamp")
    suspend fun purgeExpiredStories(nowTimestamp: Long)

    // --- CONTACTS ---
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%' OR id LIKE '%' || :query || '%' OR (:query = 'online' AND isOnline = 1) OR (:query = 'offline' AND isOnline = 0) ORDER BY name ASC")
    fun searchContacts(query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllContactsDirect(): List<ContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("UPDATE contacts SET tag = :tag WHERE id = :contactId")
    suspend fun updateContactTag(contactId: String, tag: String)

    @Query("UPDATE contacts SET isBlocked = :isBlocked WHERE id = :contactId")
    suspend fun updateContactBlockedStatus(contactId: String, isBlocked: Boolean)

    @Query("UPDATE contacts SET presenceStatus = :presenceStatus, isOnline = :isOnline WHERE id = :contactId")
    suspend fun updateContactPresenceStatus(contactId: String, presenceStatus: String, isOnline: Boolean)

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND textContent LIKE '%' || :query || '%' ORDER BY timestamp ASC")
    fun searchMessagesInChat(chatId: String, query: String): Flow<List<MessageEntity>>

    // --- CLOUD ACCOUNTS ---
    @Query("SELECT * FROM cloud_accounts")
    fun getAllCloudAccounts(): Flow<List<CloudAccountEntity>>

    @Query("SELECT * FROM cloud_accounts")
    suspend fun getAllCloudAccountsDirect(): List<CloudAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloudAccount(account: CloudAccountEntity)

    @Delete
    suspend fun deleteCloudAccount(account: CloudAccountEntity)

    @Query("UPDATE cloud_accounts SET lastSyncTimestamp = :timestamp WHERE id = :accountId")
    suspend fun updateAccountLastSync(accountId: String, timestamp: Long)
}
