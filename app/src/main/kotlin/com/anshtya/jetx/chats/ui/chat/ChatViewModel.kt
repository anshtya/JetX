package com.anshtya.jetx.chats.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations
import com.anshtya.jetx.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatsRepository: ChatsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val chatArgs = savedStateHandle.toRoute<ChatsDestinations.Chat>()

    private val chatId = MutableStateFlow<Int?>(null)

    private val _recipientUser = MutableStateFlow<RecipientUser?>(null)
    val recipientUser = _recipientUser.asStateFlow()

    init {
        viewModelScope.launch {
            when {
                chatArgs.chatId != null -> {
                    chatId.update { chatArgs.chatId }
                    val userId = chatsRepository.getChatRecipientId(chatArgs.chatId)
                    val user = profileRepository.getProfile(userId!!)

                    _recipientUser.update {
                        RecipientUser(
                            id = user!!.id,
                            username = user.username,
                            pictureUrl = user.pictureUrl
                        )
                    }
                }
                chatArgs.recipientId != null -> {
                    val recipientId = UUID.fromString(chatArgs.recipientId)
                    chatId.update { chatsRepository.getChatId(recipientId) }

                    _recipientUser.update {
                        RecipientUser(
                            id = recipientId,
                            username = chatArgs.username!!,
                            pictureUrl = chatArgs.pictureUrl
                        )
                    }
                }
            }
        }
    }

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
            val recipientUser = _recipientUser.value
            chatsRepository.sendChatMessage(
                recipientId = recipientUser!!.id,
                text = message
            )
            if (chatId.value == null) {
                chatId.update { chatsRepository.getChatId(recipientUser.id) }
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