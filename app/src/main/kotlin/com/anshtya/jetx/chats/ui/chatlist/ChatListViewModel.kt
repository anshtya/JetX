package com.anshtya.jetx.chats.ui.chatlist

import android.app.NotificationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.core.model.Chat
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatsRepository: ChatsRepository,
    private val profileRepository: ProfileRepository,
    private val notificationManager: NotificationManager,
    private val messageUpdatesListener: MessageUpdatesListener
) : ViewModel() {
    private var searchProfileJob: Job? = null

    private val _selectedFilter = MutableStateFlow(FilterOption.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _selectedChats = MutableStateFlow<Set<Int>>(emptySet())
    val selectedChats = _selectedChats.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchSuggestions = _searchSuggestions.asStateFlow()

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

    init {
        subscribeToMessageUpdates()
    }

    private fun subscribeToMessageUpdates() {
        viewModelScope.launch {
            messageUpdatesListener.subscribe()
        }
    }

    fun onSearch() {
        searchProfileJob = viewModelScope.launch {
            val query = _searchQuery.value
            _searchSuggestions.update {
                if (query.isNotBlank()) {
                    val result = profileRepository.searchProfiles(query)
                    result.getOrElse { emptyList() }
                } else {
                    emptyList()
                }
            }
            searchProfileJob = null
        }
    }

    fun clearSearch() {
        _searchQuery.update { "" }
        _searchSuggestions.update { emptyList() }
        searchProfileJob = null
    }

    fun changeFilter(filterOption: FilterOption) {
        _selectedFilter.update { filterOption }
    }

    fun selectChat(id: Int) {
        _selectedChats.update {
            it.toMutableSet().apply { add(id) }
        }
    }

    fun unselectChat(id: Int) {
        _selectedChats.update {
            it.toMutableSet().apply { remove(id) }
        }
    }

    fun clearSelectedChats() {
        _selectedChats.update { emptySet() }
    }

    fun deleteChat() {
        viewModelScope.launch {
            chatsRepository.deleteChats(_selectedChats.value.toList())
            clearSelectedChats()
        }
    }

    fun archiveChat() {
        viewModelScope.launch {
            chatsRepository.archiveChats(_selectedChats.value.toList())
            clearSelectedChats()
        }
    }

    fun unarchiveChat() {
        viewModelScope.launch {
            chatsRepository.unarchiveChats(_selectedChats.value.toList())
            clearSelectedChats()
        }
    }

    fun changeSearchQuery(searchQuery: String) {
        _searchQuery.update { searchQuery }
        searchProfileJob = null
    }

    fun clearNotification(chatId: Int) {
        notificationManager.cancel(chatId)
    }
}

sealed interface ChatListState {
    data object Loading : ChatListState

    data class Success(val list: List<Chat>) : ChatListState
}