package com.anshtya.jetx.chats.ui.chat

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anshtya.jetx.attachments.AttachmentManager
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations
import com.anshtya.jetx.database.model.MessageWithAttachment
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.work.WorkManagerHelper
import com.anshtya.jetx.work.WorkScheduler
import com.anshtya.jetx.work.worker.AttachmentDownloadWorker
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
    private val messagesRepository: MessagesRepository,
    private val profileRepository: ProfileRepository,
    private val attachmentManager: AttachmentManager,
    private val workScheduler: WorkScheduler,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {
    private val chatArgs = savedStateHandle.toRoute<ChatsDestinations.Chat>()

    private val chatId = MutableStateFlow<Int?>(null)

    private val _recipientUser = MutableStateFlow<RecipientUser?>(null)
    val recipientUser = _recipientUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _selectedMessages = MutableStateFlow<Set<Int>>(emptySet())
    val selectedMessages = _selectedMessages.asStateFlow()

    val chatMessages: StateFlow<List<MessageWithAttachment>> = chatId
        .filterNotNull()
        .flatMapLatest { messagesRepository.getChatMessages(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        initializeChatData()
    }

    private fun initializeChatData() {
        viewModelScope.launch {
            when {
                chatArgs.chatId != null -> {
                    chatId.update { chatArgs.chatId }
                    val userId = chatsRepository.getChatRecipientId(chatArgs.chatId)
                    val user = profileRepository.getProfile(userId)

                    if (user == null) {
                        _errorMessage.update { "User not found" }
                        return@launch
                    }

                    _recipientUser.update {
                        RecipientUser(
                            id = user.id,
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
            chatId.value?.let { chatsRepository.setCurrentChatId(it) }
        }
    }

    fun sendMessage(
        message: String?,
        attachmentUri: Uri? = null
    ) {
        viewModelScope.launch {
            val recipientUser = _recipientUser.value!!
            messagesRepository.sendChatMessage(
                recipientId = recipientUser.id,
                text = message,
                attachmentUri = attachmentUri
            )
            if (chatId.value == null) {
                chatId.update { chatsRepository.getChatId(recipientUser.id) }
                chatsRepository.setCurrentChatId(chatId.value)
            }
        }
    }

    fun sendAttachment(uri: Uri) {
        viewModelScope.launch {
            // Content URI must be converted to File URI
            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                try {
                    attachmentManager.saveImage(uri)
                } catch (_: Exception) {
                    _errorMessage.update { "An error occurred" }
                    return@launch
                }
                sendMessage(message = null, attachmentUri = uri)
            } else {
                sendMessage(message = null, attachmentUri = uri)
            }
        }
    }

    fun downloadAttachment(attachmentId: Int, messageId: Int) {
        workScheduler.createAttachmentDownloadWork(attachmentId, messageId)
    }

    fun cancelAttachmentDownload(attachmentId: Int, messageId: Int) {
        workManagerHelper.cancelWorkByUniqueName(
            AttachmentDownloadWorker.generateWorkerName(attachmentId, messageId)
        )
    }

    fun markChatMessagesAsSeen() {
        viewModelScope.launch {
            val id = chatId.value
            if (id != null) {
                messagesRepository.markChatMessagesAsSeen(id)
            }
        }
    }

    fun selectMessage(id: Int) {
        _selectedMessages.update {
            it.toMutableSet().apply { add(id) }
        }
    }

    fun unselectMessage(id: Int) {
        _selectedMessages.update {
            it.toMutableSet().apply { remove(id) }
        }
    }

    fun clearSelectedMessages() {
        _selectedMessages.update { emptySet() }
    }

    fun deleteMessages() {
        viewModelScope.launch {
            messagesRepository.deleteMessages(_selectedMessages.value.toList())
            clearSelectedMessages()
        }
    }

    fun errorShown() {
        _errorMessage.update { null }
    }

    override fun onCleared() {
        super.onCleared()
        chatsRepository.setCurrentChatId(null)
    }
}