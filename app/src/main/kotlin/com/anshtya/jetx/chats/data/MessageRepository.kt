package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.MessageInsertData
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getChatMessages(chatId: Int): Flow<DateChatMessages>

    suspend fun insertMessage(messageInsertData: MessageInsertData)
}