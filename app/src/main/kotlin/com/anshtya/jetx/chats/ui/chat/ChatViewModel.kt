package com.anshtya.jetx.chats.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.anshtya.jetx.chats.ui.navigation.ChatsDestinations.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val chat = savedStateHandle.toRoute<Chat>()
}