package com.anshtya.jetx.chats.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessageRepository
import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.MessageInsertData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val messageRepository: MessageRepository,
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    private val chatUserArgs = savedStateHandle.getStateFlow<ChatUserArgs?>(
        key = "chatUserArgs", initialValue = null
    )

    private val chatId = MutableStateFlow<Int?>(null)
    private val combinedChatId: StateFlow<Int?> = combine(
        chatUserArgs, chatId
    ) { userArgs, chatId ->
        chatId ?: if (userArgs != null) {
            userArgs.chatId ?: chatsRepository.getChatInfo(userArgs.recipientId)?.id
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

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

    val chatMessages: StateFlow<DateChatMessages> = combinedChatId
        .filterNotNull()
        .flatMapLatest { chatId -> messageRepository.getChatMessages(chatId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateChatMessages(emptyMap())
        )

    fun sendMessage(message: String) {
        val recipientUser = recipientUser.value ?: return

        viewModelScope.launch {
            if (combinedChatId.value == null) {
                chatId.update { chatsRepository.createChat(recipientUser.id) }
            }
            messageRepository.insertMessage(
                MessageInsertData(
                    chatId = combinedChatId.value!!,
                    recipientId = recipientUser.id,
                    message = message
                )
            )
        }
    }
}