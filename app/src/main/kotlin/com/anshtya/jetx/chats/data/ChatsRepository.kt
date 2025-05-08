package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatsRepository {
    val currentChatId: Int?

    fun setCurrentChatId(id: Int?)

    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun getChatId(recipientId: UUID): Int?

    suspend fun getChatRecipientId(chatId: Int): UUID

    suspend fun deleteChats(chatIds: List<Int>)

    suspend fun archiveChats(chatIds: List<Int>)

    suspend fun unarchiveChats(chatIds: List<Int>)
}