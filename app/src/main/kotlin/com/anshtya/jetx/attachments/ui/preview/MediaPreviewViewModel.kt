package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.chats.domain.SendChatMessageUseCase
import com.anshtya.jetx.core.coroutine.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaPreviewViewModel @Inject constructor(
    private val attachmentRepository: AttachmentRepository,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    // Set by processIncomingData; null until the first intent has been handled.
    private var sendTarget: SendTarget? = null

    private val _sendItems = MutableStateFlow(emptyList<SendItem>())

    private val _currentItemIndex = MutableStateFlow(0)
    val currentItemIndex = _currentItemIndex.asStateFlow()

    private val _navigateToChat = MutableStateFlow(false)
    val navigateToChat = _navigateToChat.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val mediaPreviewUiState: StateFlow<MediaPreviewUiState> = _sendItems
        .mapLatest(MediaPreviewUiState::DataLoaded)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MediaPreviewUiState.Loading
        )

    fun processIncomingData(
        target: SendTarget,
        uris: List<Uri>
    ) {
        sendTarget = target

        viewModelScope.launch {
            val preUploadUris = mutableListOf<Uri>()
            uris.map { uri ->
                async {
                    val preUploadResult = attachmentRepository.saveAttachmentBeforeUpload(uri)
                    preUploadResult.onSuccess {
                        preUploadUris.add(it)
                    }.onFailure {
                        _errorMessage.update { "Error processing attachment" }
                        return@async
                    }
                }
            }.awaitAll()
            _sendItems.update {
                val newList = it.toMutableList()
                newList.apply { addAll(preUploadUris.map { uri -> SendItem(uri) }) }
            }
        }
    }

    fun onCaptionChange(index: Int, newCaption: String) {
        _sendItems.update {
            val newList = it.toMutableList()
            newList.apply {
                this[index] = this[index].copy(caption = newCaption)
            }
        }
    }

    fun onSelectItem(index: Int) {
        _currentItemIndex.update { index }
    }

    fun onSendClick() {
        val target = sendTarget ?: return
        viewModelScope.launch {
            when (target) {
                is SendTarget.Chats -> {
                    target.chatIds.map { chatId ->
                        async {
                            _sendItems.value.map { item ->
                                async {
                                    sendChatMessageUseCase(
                                        chatId = chatId,
                                        text = item.caption.ifBlank { null },
                                        attachmentUri = item.uri
                                    ).onFailure {
                                        _errorMessage.update { "Error sending message" }
                                        return@async
                                    }
                                }
                            }.awaitAll()
                        }
                    }.awaitAll()
                }

                is SendTarget.Recipient -> {
                    _sendItems.value.map { item ->
                        async {
                            sendChatMessageUseCase(
                                recipientId = target.recipientId,
                                text = item.caption.ifBlank { null },
                                attachmentUri = item.uri
                            )
                        }
                    }.awaitAll()
                }
            }
            _navigateToChat.update { true }
        }
    }

    fun discardMedia() {
        viewModelScope.launch {
            val uris = _sendItems.value.map { it.uri }
            withContext(ioDispatcher) {
                uris.map { uri -> async { uri.toFile().delete() } }.awaitAll()
            }
            _navigateToChat.update { true }
        }
    }

    fun onErrorShown() {
        _errorMessage.update { null }
    }
}

sealed interface MediaPreviewUiState {
    data object Loading : MediaPreviewUiState
    data class DataLoaded(val data: List<SendItem>) : MediaPreviewUiState
}

/** Where the previewed media is sent. */
sealed interface SendTarget {
    /** Existing chats, e.g. when sharing from the send screen. */
    data class Chats(val chatIds: List<Int>) : SendTarget

    /** A single recipient, e.g. when sharing from a chat that may not exist yet. */
    data class Recipient(val recipientId: UUID) : SendTarget
}

data class SendItem(
    val uri: Uri,
    val caption: String = ""
)