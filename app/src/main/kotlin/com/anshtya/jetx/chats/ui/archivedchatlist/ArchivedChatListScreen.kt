package com.anshtya.jetx.chats.ui.archivedchatlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val selectedChats by viewModel.selectedChats.collectAsStateWithLifecycle()

    ArchivedChatListScreen(
        state = archivedChatListState,
        selectedChats = selectedChats,
        onChatClick = onNavigateToChat,
        onSelectChat = viewModel::selectChat,
        onUnselectChat = viewModel::unselectChat,
        onClearSelectedChats = viewModel::clearSelectedChats,
        onUnarchiveClick = viewModel::unarchiveChat,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchivedChatListScreen(
    state: ChatListState,
    selectedChats: Set<Int>,
    onChatClick: (ChatUserArgs) -> Unit,
    onSelectChat: (Int) -> Unit,
    onUnselectChat: (Int) -> Unit,
    onClearSelectedChats: () -> Unit,
    onUnarchiveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedChatCount = remember(selectedChats) { selectedChats.size }
    var showDeleteChatDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteChatDialog) {
        DeleteChatDialog(
            chatCount = selectedChatCount,
            onDismissRequest = { showDeleteChatDialog = false },
            onConfirmClick = {
                showDeleteChatDialog = false
            }
        )
    }

    if (state is ChatListState.Success) {
        val chatsSelected by remember(selectedChatCount > 0) {
            mutableStateOf(selectedChatCount > 0)
        }
        BackHandler(chatsSelected) {
            onClearSelectedChats()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.archived)) },
                    navigationIcon = { BackButton(onClick = onBackClick) },
                    actions = {
                        if (chatsSelected) {
                            IconButton(
                                onClick = onUnarchiveClick,
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
                    }
                )
            },
        ) { innerPadding ->
            ChatList(
                chatList = state.list,
                selectedChats = selectedChats,
                onChatClick = onChatClick,
                onSelectChat = onSelectChat,
                onUnselectChat = onUnselectChat,
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
            onChatClick = {},
            onSelectChat = {},
            onUnselectChat = {},
            onClearSelectedChats = {},
            onUnarchiveClick = {},
            onBackClick = {}
        )
    }
}