package com.anshtya.jetx.chats.ui.archivedchatlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chatlist.ChatListState
import com.anshtya.jetx.chats.ui.chatlist.ChatListViewModel
import com.anshtya.jetx.chats.ui.components.ChatList
import com.anshtya.jetx.chats.ui.components.ChatListScaffold
import com.anshtya.jetx.chats.ui.components.DeleteChatDialog
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.sampledata.sampleChats

@Composable
fun ArchivedChatListRoute(
    onNavigateToChat: (ChatUserArgs) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ChatListViewModel
) {
    val archivedChatListState by viewModel.archivedChatList.collectAsStateWithLifecycle()
    val selectedChatCount by viewModel.selectedChatCount.collectAsStateWithLifecycle()
    val selectedChats by viewModel.selectedChats.collectAsStateWithLifecycle()

    ArchivedChatListScreen(
        state = archivedChatListState,
        selectedChats = selectedChats,
        selectedChatCount = selectedChatCount,
        onChatClick = onNavigateToChat,
        onChatLongClick = viewModel::selectChat,
        onClearSelectedChats = viewModel::clearSelectedChats,
        onBackClick = onBackClick
    )
}

@Composable
private fun ArchivedChatListScreen(
    state: ChatListState,
    selectedChats: Set<Int>,
    selectedChatCount: Int,
    onChatClick: (ChatUserArgs) -> Unit,
    onChatLongClick: (Int) -> Unit,
    onClearSelectedChats: () -> Unit,
    onBackClick: () -> Unit
) {
    var showDeleteChatDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteChatDialog) {
        DeleteChatDialog(
            chatCount = selectedChatCount,
            onDismissRequest = { showDeleteChatDialog = false },
            onConfirmClick = { deleteMedia ->
                showDeleteChatDialog = false
            }
        )
    }

    if (state is ChatListState.Success) {
        ChatListScaffold(
            selectedChatCount = selectedChatCount,
            onClearSelectedChats = onClearSelectedChats,
            topBarTitle = {
                Text(text = stringResource(id = R.string.archived))
            },
            topBarActions = { chatsSelected ->
                if (chatsSelected) {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Unarchive,
                            contentDescription = stringResource(id = R.string.unarchive_chat)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteChatDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(id = R.string.delete_chat)
                        )
                    }
                }
            },
            topBarNavigationIcon = { BackButton(onClick = onBackClick) }
        ) { innerPadding, chatsSelected ->
            ChatList(
                chatList = state.list,
                selectedChats = selectedChats,
                onChatClick = onChatClick,
                onChatLongClick = onChatLongClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}

@Preview
@Composable
private fun ArchivedChatsScreenPreview() {
    ComponentPreview {
        ArchivedChatListScreen(
            state = ChatListState.Success(sampleChats),
            selectedChats = emptySet(),
            selectedChatCount = 0,
            onChatClick = {},
            onChatLongClick = {},
            onClearSelectedChats = {},
            onBackClick = {}
        )
    }
}