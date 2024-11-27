package com.anshtya.jetx.chats.data

import kotlinx.coroutines.flow.Flow

interface ChatsRepository {
    val chats: Flow<List<Chat>>

    fun deleteChat(id: Int): Int
}