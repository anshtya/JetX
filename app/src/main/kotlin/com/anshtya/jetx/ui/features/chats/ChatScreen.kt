package com.anshtya.jetx.ui.features.chats

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val text = viewModel.text

    ChatScreen(text)
}

@Composable
private fun ChatScreen(
    text: String
) {
    Text(text)
}