package com.anshtya.jetx.chats.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.model.DateChatMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
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

    private var messageSeenJob: Job? = null

    val recipientUser: StateFlow<RecipientUser?> = chatUserArgs
        .filterNotNull()
        .mapLatest { userArgs ->
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

    val chatMessages: StateFlow<DateChatMessages> = chatUserArgs
        .filterNotNull()
        .flatMapLatest { userArgs ->
            chatsRepository.getChatMessages(
                chatId = userArgs.chatId ?: chatsRepository.getChatId(userArgs.recipientId)!!
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateChatMessages(emptyMap())
        )

    fun sendMessage(message: String) {
        val recipientUser = recipientUser.value ?: return

        viewModelScope.launch {
            chatsRepository.insertChatMessage(
                recipientId = recipientUser.id,
                text = message
            )
        }
    }

    fun markChatSeen() {
        messageSeenJob = viewModelScope.launch {
            chatsRepository.markChatAsRead(chatUserArgs.value?.recipientId!!)
            messageSeenJob = null
        }
    }

    fun markMessageSeen(messageId: UUID) {
        if (messageSeenJob != null) return

        messageSeenJob = viewModelScope.launch {
            chatsRepository.markChatMessageAsSeen(messageId)
            messageSeenJob = null
        }
    }
}