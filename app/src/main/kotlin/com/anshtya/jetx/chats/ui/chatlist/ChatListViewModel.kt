package com.anshtya.jetx.chats.ui.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.common.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatListViewModel @Inject constructor(
    chatsRepository: ChatsRepository
) : ViewModel() {
    private val filters = MutableStateFlow(1)

    val chatList: StateFlow<List<Chat>> = filters
        .flatMapLatest {
            chatsRepository.getChats(
                showArchivedChats = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}

data class ChatListUiState(
    val chatList: List<Chat>
)