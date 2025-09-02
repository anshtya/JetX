package com.anshtya.jetx.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anshtya.jetx.core.database.entity.MessageEntity
import com.anshtya.jetx.core.database.model.MessageStatus
import java.time.ZonedDateTime
import java.util.UUID

@Dao
interface MessageDao {
    @Query("SELECT chat_id FROM message WHERE uid = :messageId")
    suspend fun getMessageChatId(messageId: UUID): Int

    @Query("SELECT * FROM message WHERE uid = :messageId")
    suspend fun getMessage(messageId: UUID): MessageEntity

    @Query("""
        SELECT uid FROM message 
        WHERE chat_id = :chatId AND status = :receivedStatus
    """)
    suspend fun getUnreadMessagesId(
        chatId: Int,
        receivedStatus: MessageStatus = MessageStatus.RECEIVED
    ): List<UUID>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM message WHERE id in (:ids)")
    suspend fun deleteMessages(ids: List<Int>)

    @Query("UPDATE message SET status = :status WHERE uid = :uid")
    suspend fun updateMessageStatus(
        uid: UUID,
        status: MessageStatus
    )

    @Query("UPDATE message SET text = :text WHERE id = :id")
    suspend fun updateMessageText(
        id: Int,
        text: String
    )

    @Query("""
        UPDATE message 
        SET status = :seenStatus 
        WHERE chat_id = :chatId AND status = :receivedStatus AND created_at <= :time
    """)
    suspend fun markMessagesAsRead(
        chatId: Int,
        time: ZonedDateTime = ZonedDateTime.now(),
        seenStatus: MessageStatus = MessageStatus.SEEN,
        receivedStatus: MessageStatus = MessageStatus.RECEIVED
    )
}