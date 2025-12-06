package com.anshtya.jetx.chats.ui.archivedchatlist

import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.ui.ChatsBaseViewModel
import com.anshtya.jetx.chats.ui.chatlist.ChatListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedChatListViewModel @Inject constructor(
    private val chatsRepository: ChatsRepository
) : ChatsBaseViewModel() {
    val archivedChatList: StateFlow<ChatListState> = chatsRepository.getArchivedChats()
        .map(ChatListState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ChatListState.Loading
        )

    fun unarchiveChat() {
        viewModelScope.launch {
            chatsRepository.unarchiveChats(mutableSelectedChats.value.toList())
            clearSelectedChats()
        }
    }
}