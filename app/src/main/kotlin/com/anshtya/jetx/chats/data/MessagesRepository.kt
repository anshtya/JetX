package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MessagesRepository {
    fun getChatMessages(chatId: Int): Flow<DateChatMessages>

    suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?
    )

    suspend fun sendChatMessage(
        chatId: Int,
        text: String?
    )

    suspend fun markChatMessagesAsSeen(chatId: Int)
}