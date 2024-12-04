package com.anshtya.jetx.chats.data.fake

import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.chats.data.ChatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FakeChatsRepository @Inject constructor() : ChatsRepository {
    private val _chats = MutableStateFlow(fakeChats)
    override val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    override fun deleteChat(id: Int): Int {
        _chats.update {
            it.toMutableList().apply {
                removeIf { chat -> chat.id == id }
            }
        }
        return id
    }
}