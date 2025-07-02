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
    var capturedMediaUri: Uri? = null

    private val _navigateToPreview = MutableStateFlow(false)
    val navigateToPreview = _navigateToPreview.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onImageCapture(bitmap: Bitmap) {
        viewModelScope.launch {
            val result = attachmentRepository.saveBitmapImageBeforeUpload(bitmap)
            if (result.isSuccess) {
                capturedMediaUri = result.getOrNull()
                _navigateToPreview.update { true }
            } else {
                _errorMessage.update { "Failed to save image" }
            }
        }
    }

    fun onVideoCapture(uri: Uri) {
        viewModelScope.launch {
            val result = attachmentRepository.saveAttachmentBeforeUpload(uri)
            if (result.isSuccess) {
                capturedMediaUri = result.getOrNull()
                _navigateToPreview.update { true }
            } else {
                _errorMessage.update { "Failed to save video" }
            }
        }
    }

    fun onNavigate() {
        _navigateToPreview.update { false }
    }

    fun onErrorShown() {
        _errorMessage.update { null }
    }
}