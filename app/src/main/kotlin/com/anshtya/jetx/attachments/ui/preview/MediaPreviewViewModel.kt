package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaPreviewViewModel @Inject constructor(

) : ViewModel() {
    private val _recipients = mutableListOf<Int>()

    private val _uris = MutableStateFlow(mapOf<Uri, String>())
    val uris = _uris.asStateFlow()

    private val _currentUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val currentUri = _currentUri.asStateFlow()

    private val _navigateToChat = MutableStateFlow<Boolean>(false)
    val navigateToChat = _navigateToChat.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun addUri(uri: Uri) {
        _uris.update {
            val newMap = it.toMutableMap()
            newMap.apply { put(uri, "") }
        }
        if (_currentUri.value == Uri.EMPTY) {
            _currentUri.update { uri }
        }
    }

    fun addUris(uris: List<Uri>) {
        _uris.update {
            val newMap = it.toMutableMap()
            newMap.apply {
                putAll(uris.map { uri -> Pair(uri, "") })
            }
        }
        if (_currentUri.value == Uri.EMPTY) {
            _currentUri.update { uris.first() }
        }
    }

    fun addRecipients(recipients: List<Int>) {
        _recipients.addAll(recipients)
    }

    fun onSelectUri(uri: Uri) {
        _currentUri.update { uri }
    }

    fun onSend() {
        viewModelScope.launch {

        }
    }

    fun onErrorShown() {
        _errorMessage.update { null }
    }
}