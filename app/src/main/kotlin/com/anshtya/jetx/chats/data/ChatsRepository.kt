package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.database.model.ChatIds
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatsRepository {
    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun getChatIds(recipientId: UUID): ChatIds?

    suspend fun getChatIds(chatId: Int): ChatIds?

    suspend fun deleteChats(chatIds: List<Int>)

    suspend fun archiveChats(chatIds: List<Int>)

    suspend fun unarchiveChats(chatIds: List<Int>)
}