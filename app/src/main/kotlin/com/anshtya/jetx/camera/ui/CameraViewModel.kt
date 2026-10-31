package com.anshtya.jetx.camera.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.attachments.data.AttachmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {
    /** The captured media to preview. `null` when there is nothing to navigate to. */
    private val _previewUri = MutableStateFlow<Uri?>(null)
    val previewUri = _previewUri.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onImageCapture(bitmap: Bitmap) {
        viewModelScope.launch {
            attachmentRepository.saveBitmapImageBeforeUpload(bitmap)
                .onSuccess { uri -> _previewUri.update { uri } }
                .onFailure { _errorMessage.update { "Failed to save image" } }
        }
    }

    fun onVideoCapture(uri: Uri) {
        viewModelScope.launch {
            attachmentRepository.saveAttachmentBeforeUpload(uri)
                .onSuccess { savedUri -> _previewUri.update { savedUri } }
                .onFailure { _errorMessage.update { "Failed to save video" } }
        }
    }

    fun onNavigate() {
        _previewUri.update { null }
    }

    fun onErrorShown() {
        _errorMessage.update { null }
    }
}