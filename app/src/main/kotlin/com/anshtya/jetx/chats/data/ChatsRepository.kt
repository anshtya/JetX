package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatsRepository {
    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun getChatId(recipientId: UUID): Int?

    suspend fun getChatRecipientId(chatId: Int): UUID?

    fun getChatMessages(chatId: Int): Flow<DateChatMessages>

    suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?
    )

    suspend fun deleteChats(chatIds: List<Int>)

    suspend fun markChatMessagesAsSeen(chatId: Int)
}