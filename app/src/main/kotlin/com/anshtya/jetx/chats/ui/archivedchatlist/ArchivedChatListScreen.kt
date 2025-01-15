package com.anshtya.jetx.chats.ui.archivedchatlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chatlist.ChatListState
import com.anshtya.jetx.chats.ui.chatlist.ChatListViewModel
import com.anshtya.jetx.chats.ui.components.ChatList
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.sampledata.sampleChats
import com.anshtya.jetx.util.Constants.defaultPadding

@Composable
fun ArchivedChatListRoute(
    onNavigateToChat: (ChatUserArgs) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ChatListViewModel
) {
    val archivedChatListState by viewModel.archivedChatList.collectAsStateWithLifecycle()

    ArchivedChatListScreen(
        state = archivedChatListState,
        onChatClick = onNavigateToChat,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchivedChatListScreen(
    state: ChatListState,
    onChatClick: (ChatUserArgs) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.archived))
                },
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = defaultPadding)
        ) {
            if (state is ChatListState.Success) {
                ChatList(
                    chatList = state.list,
                    onChatClick = onChatClick,
                    onChatLongClick = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview
@Composable
private fun ArchivedChatsScreenPreview() {
    ComponentPreview {
        ArchivedChatListScreen(
            state = ChatListState.Success(sampleChats),
            onChatClick = {},
            onBackClick = {}
        )
    }
}