package com.anshtya.jetx.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    chatsRepository: ChatsRepository
) : ViewModel() {
    private val _selectedChatIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedChatIds = _selectedChatIds.asStateFlow()

    val members: StateFlow<List<ChatMember>> = chatsRepository.getChats(
        showFavoriteChats = false,
        showUnreadChats = false
    ).map { chats ->
        chats.map {
            ChatMember(
                id = it.id,
                username = it.username,
                profilePicture = it.profilePicture
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addOrRemoveChatId(id: Int) {
        _selectedChatIds.update {
            it.toMutableSet().apply {
                if (contains(id)) remove(id) else add(id)
            }
        }
    }
}

data class ChatMember(
    val id: Int,
    val username: String,
    val profilePicture: String?
)