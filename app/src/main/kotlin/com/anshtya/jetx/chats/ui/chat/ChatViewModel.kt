package com.anshtya.jetx.chats.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.model.DateChatMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    private val chatUserArgs = savedStateHandle.getStateFlow<ChatUserArgs?>(
        key = "chatUserArgs", initialValue = null
    )
    private val chatId = MutableStateFlow<Int?>(null)

    val recipientUser: StateFlow<RecipientUser?> = chatUserArgs
        .filterNotNull()
        .mapLatest { userArgs ->
            chatId.update { userArgs.chatId }
            RecipientUser(
                id = userArgs.recipientId,
                username = userArgs.username,
                pictureUrl = userArgs.pictureUrl
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val chatMessages: StateFlow<DateChatMessages> = chatId
        .filterNotNull()
        .flatMapLatest { chatsRepository.getChatMessages(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateChatMessages(emptyMap())
        )

    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatsRepository.sendChatMessage(
                recipientId = recipientUser.value!!.id,
                text = message
            )

            if (chatId.value == null) {
                chatId.update { chatsRepository.getChatId(chatUserArgs.value!!.recipientId) }
            }
        }
    }

    fun markChatMessagesAsSeen() {
        viewModelScope.launch {
            val id = chatId.value
            if (id != null) {
                chatsRepository.markChatMessagesAsSeen(id)
            }
        }
    }
}