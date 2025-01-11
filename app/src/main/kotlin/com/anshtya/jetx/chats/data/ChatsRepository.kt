package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.ChatInfo
import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatsRepository {
    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun getChatInfo(recipientId: UUID): ChatInfo?

    suspend fun createChat(recipientId: UUID): Int

    suspend fun deleteChats(chatIds: List<Int>)
}