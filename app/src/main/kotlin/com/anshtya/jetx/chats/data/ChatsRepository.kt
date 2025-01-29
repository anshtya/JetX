package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatsRepository {
    suspend fun subscribeChanges()

    suspend fun unsubscribeChanges()

    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun getChatId(recipientId: UUID): Int?

    fun getChatMessages(chatId: Int): Flow<DateChatMessages>

    suspend fun insertChatMessage(
        recipientId: UUID,
        text: String? = null,
        attachment: String? = null
    )

    suspend fun deleteChats(chatIds: List<Int>)

    suspend fun markChatMessagesAsSeen(chatId: Int)
}