package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anshtya.jetx.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE chat_id = :chatId")
    fun getChatMessages(chatId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE id = :messageId")
    suspend fun getMessage(messageId: UUID): MessageEntity?

    @Query("SELECT * FROM message WHERE is_starred = 1")
    suspend fun getStarredMessages(): List<MessageEntity>

    @Upsert
    suspend fun upsertMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessages(messages: List<MessageEntity>)
}