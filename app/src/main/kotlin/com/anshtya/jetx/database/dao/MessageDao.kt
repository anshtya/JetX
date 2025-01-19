package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.util.UUID

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE chat_id = :chatId ORDER BY created_at DESC")
    fun getChatMessages(chatId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE id = :messageId")
    suspend fun getChatMessage(messageId: UUID): MessageEntity

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM message 
            WHERE recipient_id = :recipientId AND chat_id = :chatId AND status = :receivedStatus AND created_at <= :timestamp
        )
    """)
    suspend fun getRecentUnreadChatMessage(
        chatId: Int,
        recipientId: UUID,
        receivedStatus: MessageStatus = MessageStatus.RECEIVED,
        timestamp: ZonedDateTime = ZonedDateTime.now()
    ): Boolean

    @Query("""
        SELECT id FROM message 
        WHERE recipient_id = :recipientId AND chat_id = :chatId AND status = :receivedStatus
    """)
    suspend fun getUnreadMessagesId(
        chatId: Int,
        recipientId: UUID,
        receivedStatus: MessageStatus = MessageStatus.RECEIVED
    ): List<UUID>

    @Upsert
    suspend fun upsertMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessages(messages: List<MessageEntity>)

    @Query("UPDATE message SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(
        id: UUID,
        status: MessageStatus
    )

    @Query("""
        UPDATE message 
        SET status = :seenStatus 
        WHERE recipient_id = :recipientId AND chat_id = :chatId AND status = :receivedStatus
    """)
    suspend fun markMessagesAsRead(
        chatId: Int,
        recipientId: UUID,
        seenStatus: MessageStatus = MessageStatus.SEEN,
        receivedStatus: MessageStatus = MessageStatus.RECEIVED
    )
}