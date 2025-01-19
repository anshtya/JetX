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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    private val _selectedFilter = MutableStateFlow(FilterOption.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    init {
        viewModelScope.launch { chatsRepository.subscribeChanges() }
    }

    val chatList: StateFlow<ChatListState> = _selectedFilter
        .flatMapLatest { filter ->
            chatsRepository.getChats(
                showFavoriteChats = filter == FilterOption.FAVORITES,
                showUnreadChats = filter == FilterOption.UNREAD,
            )
        }
        .map(ChatListState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ChatListState.Loading
        )

    val archivedChatList: StateFlow<ChatListState> = chatsRepository.getArchivedChats()
        .map(ChatListState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ChatListState.Loading
        )

    val archivedChatsEmpty: StateFlow<Boolean> = archivedChatList
        .map { state -> state is ChatListState.Success && state.list.isEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true
        )

    fun changeFilter(filterOption: FilterOption) {
        _selectedFilter.update { filterOption }
    }

    override fun onCleared() {
        super.onCleared()
        runBlocking { chatsRepository.unsubscribeChanges() }
    }
}

sealed interface ChatListState {
    data object Loading : ChatListState

    data class Success(val list: List<Chat>) : ChatListState
}