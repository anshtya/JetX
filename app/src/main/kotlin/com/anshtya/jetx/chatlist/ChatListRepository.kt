package com.anshtya.jetx.chatlist

import kotlinx.coroutines.flow.Flow

interface ChatListRepository {
    val chatList: Flow<List<ChatListItem>>

    fun deleteChat(id: Int): Int
}