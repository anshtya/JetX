package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {
    fun getChats(
        showArchivedChats: Boolean
    ): Flow<List<Chat>>

    suspend fun deleteChats(chatIds: List<Int>)
}