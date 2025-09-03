package com.anshtya.jetx.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.anshtya.jetx.core.database.entity.ChatEntity
import com.anshtya.jetx.core.database.model.ChatWithRecentMessage
import com.anshtya.jetx.core.database.model.MessageStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ChatDao {
    @Transaction
    @Query(
        value = """
            SELECT 
                chat.id, chat.recipient_id,
                message.text, message.status, message.created_at, message.sender_id,
                user_profile.username, user_profile.profile_picture,
                (
                    SELECT COUNT(message.id) FROM message
                    WHERE message.chat_id = chat.id AND chat.recipient_id == message.sender_id
                    AND message.status = :receivedStatus
                ) AS unread_count
            FROM recent_message
            JOIN chat ON recent_message.chat_id = chat.id
            JOIN message ON recent_message.message_id = message.id
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
                    THEN message.status = :receivedStatus
                    ELSE 1
                END
            ORDER BY message.created_at DESC
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

    @Insert
    suspend fun insertChat(chatEntity: ChatEntity): Long

    @Query("DELETE FROM chat WHERE id in (:chatIds)")
    suspend fun deleteChats(chatIds: List<Int>)

    @Query("UPDATE chat SET is_archived = 1 WHERE id in (:chatIds)")
    suspend fun archiveChat(chatIds: List<Int>)

    @Query("UPDATE chat SET is_archived = 0 WHERE id in (:chatIds)")
    suspend fun unarchiveChat(chatIds: List<Int>)
}