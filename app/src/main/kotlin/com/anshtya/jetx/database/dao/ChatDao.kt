package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
                chat.id, user_profile.username, user_profile.profile_picture, 
                message.text, message.created_at, message.status
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
                    ELSE 1
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

    @Query("SELECT * FROM chat WHERE recipient_id =:recipientId")
    suspend fun getChat(recipientId: UUID): ChatEntity?

    @Insert
    suspend fun insertChat(chatEntity: ChatEntity): Long

    @Query("DELETE FROM chat WHERE id in (:chatIds)")
    suspend fun deleteChats(chatIds: List<Int>)
}