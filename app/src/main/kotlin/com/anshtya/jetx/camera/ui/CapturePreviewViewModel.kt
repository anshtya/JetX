package com.anshtya.jetx.camera.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.attachments.AttachmentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CapturePreviewViewModel @Inject constructor(
    private val attachmentManager: AttachmentManager
) : ViewModel() {
    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var savedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun saveImage(bitmap: Bitmap) {
        viewModelScope.launch {
            loading = true
            try {
                savedImageUri = attachmentManager.saveImage(bitmap).getOrThrow()
            } catch (e: Exception) {
                errorMessage = e.message
            }
            loading = false
        }
    }

    fun errorShown() {
        errorMessage = null
    }
}