package com.anshtya.jetx.chats.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.chats.data.ChatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    chatsRepository: ChatsRepository
) : ViewModel() {
    val chatList: StateFlow<List<Chat>> = chatsRepository.chats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}

data class ChatListUiState(
    val chatList: List<Chat>
)