package com.anshtya.jetx.chats.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.anshtya.jetx.chats.data.Chat

@Composable
fun ChatItem(
    chat: Chat
) {
    Text(chat.name)
}