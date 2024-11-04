package com.anshtya.jetx.ui.features.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.data.network.service.MainService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val mainService: MainService
) : ViewModel() {
    var text by mutableStateOf("")

    init {
        viewModelScope.launch {
            delay(1000L)
            try {
                val response = mainService.getUser("ansh1234")
                text = response.user
            } catch (e: Exception) {
                text = "error"
            }
        }
    }
}