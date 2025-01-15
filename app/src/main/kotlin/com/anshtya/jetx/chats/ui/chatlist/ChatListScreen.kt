package com.anshtya.jetx.chats.ui.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.components.ChatList
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.IconButtonDropdownMenu
import com.anshtya.jetx.sampledata.sampleChats
import com.anshtya.jetx.util.Constants.defaultPadding

@Composable
fun ChatListRoute(
    onNavigateToChat: (ChatUserArgs) -> Unit,
    onNavigateToArchivedChats: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ChatListViewModel
) {
    val chatListState by viewModel.chatList.collectAsStateWithLifecycle()
    val archivedChatEmpty by viewModel.archivedChatsEmpty.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    ChatListScreen(
        state = chatListState,
        archivedChatEmpty = archivedChatEmpty,
        selectedFilter = selectedFilter,
        onChatClick = onNavigateToChat,
        onFilterOptionClick = viewModel::changeFilter,
        onArchivedChatsClick = onNavigateToArchivedChats,
        onSearchButtonClick = onNavigateToSearch,
        onStarredMessagesClick = {
            // TODO: add starred messages support
        },
        onSettingsClick = onNavigateToSettings
    )
}

@Composable
private fun ChatListScreen(
    state: ChatListState,
    archivedChatEmpty: Boolean,
    selectedFilter: FilterOption,
    onChatClick: (ChatUserArgs) -> Unit,
    onFilterOptionClick: (FilterOption) -> Unit,
    onArchivedChatsClick: () -> Unit,
    onSearchButtonClick: () -> Unit,
    onStarredMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val filterOptions = remember { FilterOption.entries }

    Scaffold(
        topBar = {
            ChatListTopAppBar(
                onStarredMessagesClick = onStarredMessagesClick,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSearchButtonClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = defaultPadding)
        ) {
            FilterRow(
                filterOptions = filterOptions,
                selectedFilter = selectedFilter,
                onFilterOptionClick = onFilterOptionClick,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            if (state is ChatListState.Success) {
                ChatList(
                    chatList = state.list,
                    onChatClick = onChatClick,
                    onChatLongClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    slot = {
                        if (!archivedChatEmpty) {
                            item {
                                ArchivedChatsItem(onArchivedChatsClick = onArchivedChatsClick)
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListTopAppBar(
    onStarredMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            IconButtonDropdownMenu(
                expanded = showDropdownMenu,
                onIconClick = { showDropdownMenu = !showDropdownMenu },
                onDismissRequest = { showDropdownMenu = false },
            ) { dismissMenu ->
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.starred_messages))
                    },
                    onClick = {
                        onStarredMessagesClick()
                        dismissMenu()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.settings))
                    },
                    onClick = {
                        onSettingsClick()
                        dismissMenu()
                    }
                )
            }
        }
    )
}

@Composable
private fun FilterRow(
    filterOptions: List<FilterOption>,
    selectedFilter: FilterOption,
    onFilterOptionClick: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = filterOptions
        ) {
            FilterChip(
                selected = it == selectedFilter,
                label = {
                    Text(text = stringResource(it.displayName))
                },
                shape = RoundedCornerShape(16.dp),
                onClick = { onFilterOptionClick(it) }
            )
        }
    }
}

@Composable
fun ArchivedChatsItem(
    onArchivedChatsClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArchivedChatsClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = stringResource(id = R.string.archived),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(id = R.string.archived),
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
private fun ChatsScreenPreview() {
    ComponentPreview {
        ChatListScreen(
            state = ChatListState.Success(sampleChats),
            archivedChatEmpty = true,
            selectedFilter = FilterOption.ALL,
            onChatClick = {},
            onFilterOptionClick = {},
            onArchivedChatsClick = {},
            onSearchButtonClick = {},
            onStarredMessagesClick = {},
            onSettingsClick = {}
        )
    }
}