package com.anshtya.jetx.chats.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.chats.data.fake.fakeChats
import com.anshtya.jetx.chats.ui.components.ChatItem
import com.anshtya.jetx.chats.ui.components.MenuOption
import com.anshtya.jetx.chats.ui.components.TopAppBarDropdownMenu
import com.anshtya.jetx.common.ui.ComponentPreview

@Composable
fun ChatsRoute(
    onChatClick: (Int) -> Unit = {},
    onNavigateToSettings: () -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chatList by viewModel.chatList.collectAsStateWithLifecycle()

    ChatsScreen(
        chats = chatList,
        onChatClick = onChatClick,
        onStarredMessagesClick = {},
        onSettingsClick = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsScreen(
    chats: List<Chat>,
    onChatClick: (Int) -> Unit,
    onStarredMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TopAppBarDropdownMenu(
                        expanded = showDropdownMenu,
                        onIconClick = { showDropdownMenu = !showDropdownMenu },
                        onDismissRequest = { showDropdownMenu = false },
                        onMenuItemClick = {
                            when (it) {
                                MenuOption.STARRED_MESSAGES -> onStarredMessagesClick()
                                MenuOption.SETTINGS -> onSettingsClick()
                            }
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            items(
                items = chats,
                key = { it.id }
            ) {
                ChatItem(
                    chat = it,
                    onClick = onChatClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatsScreenPreview() {
    ComponentPreview {
        ChatsScreen(
            chats = fakeChats,
            onChatClick = {},
            onStarredMessagesClick = {},
            onSettingsClick = {}
        )
    }
}