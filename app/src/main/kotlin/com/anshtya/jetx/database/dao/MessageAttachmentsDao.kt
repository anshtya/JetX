package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.anshtya.jetx.database.model.MessageWithAttachment
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageAttachmentsDao {
    @Transaction
    @Query("""
        SELECT id, sender_id, chat_id, text, is_starred, created_at, status
        FROM message WHERE message.chat_id = :chatId
        ORDER BY id DESC
    """)
    fun getMessageWithAttachments(chatId: Int): Flow<List<MessageWithAttachment>>
}