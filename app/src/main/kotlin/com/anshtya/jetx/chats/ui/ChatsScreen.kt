package com.anshtya.jetx.chats.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.chats.ui.components.ChatItem

@Composable
fun ChatsRoute(
    onNavigateToSettings: () -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chatList by viewModel.chatList.collectAsStateWithLifecycle()

    ChatsScreen(chats = chatList)
}

@Composable
private fun ChatsScreen(
    chats: List<Chat>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = chats,
            key = { it.id }
        ) {
            ChatItem(it)
        }
    }
}