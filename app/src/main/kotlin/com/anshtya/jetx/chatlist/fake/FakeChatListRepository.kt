package com.anshtya.jetx.chatlist.fake

import com.anshtya.jetx.chatlist.ChatListItem
import com.anshtya.jetx.chatlist.ChatListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeChatListRepository : ChatListRepository {
    private val _chatList = MutableStateFlow(fakeChatList)
    override val chatList: StateFlow<List<ChatListItem>> = _chatList.asStateFlow()

    override fun deleteChat(id: Int): Int {
        _chatList.update {
            it.toMutableList().apply { removeIf{ it.id == id } }
        }
        return id
    }
}