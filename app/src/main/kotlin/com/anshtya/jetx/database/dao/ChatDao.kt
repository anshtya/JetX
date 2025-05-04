package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.util.UUID

@Dao
interface ChatDao {
    @Transaction
    @Query(
        value = """
            SELECT 
                chat.id, chat.recipient_id, chat.unread_count, chat.recent_message_text, 
                chat.recent_message_status, chat.recent_message_timestamp, chat.recent_message_sender_id,
                user_profile.username, user_profile.profile_picture
            FROM chat
            JOIN user_profile ON chat.recipient_id = user_profile.id
            AND
                CASE WHEN :showArchivedChats
                    THEN chat.is_archived = 1
                    ELSE chat.is_archived = 0
                END
            AND
                CASE WHEN :showFavoriteChats
                    THEN chat.is_favorite = 1
                    ELSE 1
                END
            AND
                CASE WHEN :showUnreadChats
                    THEN chat.recent_message_status = :receivedStatus
                    ELSE 1
                END
            ORDER BY chat.recent_message_timestamp DESC
        """
    )
    fun getChatsWithRecentMessage(
        showArchivedChats: Boolean,
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean,
        receivedStatus: String = MessageStatus.RECEIVED.name
    ): Flow<List<ChatWithRecentMessage>>

    @Query("SELECT id FROM chat WHERE recipient_id =:recipientId")
    suspend fun getChatId(recipientId: UUID): Int?

    @Query("SELECT recipient_id FROM chat WHERE id =:chatId")
    suspend fun getChatRecipientId(chatId: Int): UUID

    @Query("SELECT recent_message_text FROM chat WHERE id =:chatId")
    suspend fun getRecentMessageText(chatId: Int): String

    @Insert
    suspend fun insertChat(chatEntity: ChatEntity): Long

    @Query("DELETE FROM chat WHERE id in (:chatIds)")
    suspend fun deleteChats(chatIds: List<Int>)

    @Query("UPDATE chat SET is_archived = 1 WHERE id in (:chatIds)")
    suspend fun archiveChat(chatIds: List<Int>)

    @Query("UPDATE chat SET is_archived = 0 WHERE id in (:chatIds)")
    suspend fun unarchiveChat(chatIds: List<Int>)

    @Query("UPDATE chat SET unread_count = unread_count + 1 WHERE id = :chatId")
    suspend fun updateUnreadCount(chatId: Int)

    @Query("""
        UPDATE chat
        SET recent_message_text = :messageText, recent_message_timestamp = :messageTimestamp,
        recent_message_status = :messageStatus, recent_message_sender_id = :senderId
        WHERE id = :chatId
    """)
    suspend fun updateRecentMessage(
        chatId: Int,
        senderId: UUID,
        messageText: String,
        messageTimestamp: ZonedDateTime,
        messageStatus: MessageStatus
    )

    @Query("""
        UPDATE chat
        SET recent_message_status = :messageStatus
        WHERE id = :chatId
    """)
    suspend fun updateRecentMessageStatus(
        chatId: Int,
        messageStatus: MessageStatus
    )

    @Query("""
        UPDATE chat
        SET recent_message_text = :messageText, recent_message_status = :messageStatus
        WHERE id = :chatId
    """)
    suspend fun updateRecentMessageTextAndStatus(
        chatId: Int,
        messageText: String,
        messageStatus: MessageStatus
    )

    @Query("UPDATE chat SET unread_count = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: Int)
}