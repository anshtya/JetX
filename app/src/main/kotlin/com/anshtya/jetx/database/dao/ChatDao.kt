package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.anshtya.jetx.database.model.ChatIds
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ChatDao {
    @Transaction
    @Query(
        value = """
            SELECT 
                chat.id, chat.recipient_id, chat.unread_count,
                user_profile.username, user_profile.profile_picture, 
                message.text, message.sender_id, message.created_at, message.status
            FROM chat
            JOIN message ON chat.id = message.chat_id
            JOIN user_profile ON chat.recipient_id = user_profile.id
            WHERE 
                message.created_at = (
                    SELECT MAX(message.created_at) from message
                    WHERE chat.id = message.chat_id 
                )
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
                    THEN message.status = 'RECEIVED'
                    ELSE 1
                END
            ORDER BY message.created_at DESC
        """
    )
    fun getChatsWithRecentMessage(
        showArchivedChats: Boolean,
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<ChatWithRecentMessage>>

    @Query("SELECT id, recipient_id FROM chat WHERE recipient_id =:recipientId")
    suspend fun getChatIds(recipientId: UUID): ChatIds?

    @Query("SELECT id, recipient_id FROM chat WHERE id =:chatId")
    suspend fun getChatIds(chatId: Int): ChatIds?

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

    @Query("UPDATE chat SET unread_count = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: Int)
}