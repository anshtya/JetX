package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.anshtya.jetx.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE chat_id = :chatId")
    fun getChatMessage(chatId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE is_starred = 1")
    suspend fun getStarredMessages(): List<MessageEntity>

    @Delete
    suspend fun deleteMessages(messages: List<MessageEntity>)
}