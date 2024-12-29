package com.anshtya.jetx.chats.ui.chat

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel()
) {

    ChatScreen()
}

@Composable
private fun ChatScreen() {

}