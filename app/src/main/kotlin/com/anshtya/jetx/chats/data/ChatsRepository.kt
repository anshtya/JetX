package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {
    fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>>

    fun getArchivedChats(): Flow<List<Chat>>

    suspend fun deleteChats(chatIds: List<Int>)
}