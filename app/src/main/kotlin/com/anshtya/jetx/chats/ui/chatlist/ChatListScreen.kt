package com.anshtya.jetx.chats.ui.chatlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.components.ChatList
import com.anshtya.jetx.chats.ui.components.DeleteChatDialog
import com.anshtya.jetx.chats.ui.components.SearchTextField
import com.anshtya.jetx.chats.ui.permissions.NotificationPermissionHandler
import com.anshtya.jetx.core.model.sampledata.sampleChats
import com.anshtya.jetx.core.network.model.response.UserProfileSearchItem
import com.anshtya.jetx.core.ui.UserListItem
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.button.IconButtonDropdownMenu
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.core.ui.noRippleClickable
import com.anshtya.jetx.ui.theme.JetXTheme
import com.anshtya.jetx.util.Constants.defaultPadding

@Composable
fun ChatListRoute(
    onNavigateToChat: (ChatUserArgs) -> Unit,
    onNavigateToArchivedChats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chatListState by viewModel.chatList.collectAsStateWithLifecycle()
    val archivedChatEmpty by viewModel.archivedChatsEmpty.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedChats by viewModel.selectedChats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()

    ChatListScreen(
        state = chatListState,
        selectedChats = selectedChats,
        archivedChatEmpty = archivedChatEmpty,
        selectedFilter = selectedFilter,
        searchQuery = searchQuery,
        searchResults = searchSuggestions,
        onChatClick = {
            viewModel.clearNotification(it.chatId!!)
            onNavigateToChat(it)
        },
        onSelectChat = viewModel::selectChat,
        onUnselectChat = viewModel::unselectChat,
        onClearSelectedChats = viewModel::clearSelectedChats,
        onDeleteChatClick = viewModel::deleteChat,
        onArchiveClick = viewModel::archiveChat,
        onFilterOptionClick = viewModel::changeFilter,
        onArchivedChatsClick = onNavigateToArchivedChats,
        onSearchQueryChange = viewModel::changeSearchQuery,
        onSearch = viewModel::onSearch,
        onClearSearch = viewModel::clearSearch,
        onStarredMessagesClick = {},
        onSettingsClick = onNavigateToSettings,
        onProfileClick = onNavigateToChat
    )
}

@Composable
private fun ChatListScreen(
    state: ChatListState,
    selectedChats: Set<Int>,
    archivedChatEmpty: Boolean,
    selectedFilter: FilterOption,
    searchQuery: String,
    searchResults: List<UserProfileSearchItem>,
    onChatClick: (ChatUserArgs) -> Unit,
    onSelectChat: (Int) -> Unit,
    onUnselectChat: (Int) -> Unit,
    onClearSelectedChats: () -> Unit,
    onDeleteChatClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onFilterOptionClick: (FilterOption) -> Unit,
    onArchivedChatsClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearSearch: () -> Unit,
    onStarredMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: (ChatUserArgs) -> Unit,
) {
    val selectedChatCount = remember(selectedChats) { selectedChats.size }
    val filterOptions = remember { FilterOption.entries }
    var showDropdownMenu by remember { mutableStateOf(false) }

    var showDeleteChatDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteChatDialog) {
        DeleteChatDialog(
            chatCount = selectedChatCount,
            onDismissRequest = { showDeleteChatDialog = false },
            onConfirmClick = { deleteMedia ->
                onDeleteChatClick()
                showDeleteChatDialog = false
            }
        )
    }

    if (state is ChatListState.Success) {
        NotificationPermissionHandler()

        var searchEnabled by remember { mutableStateOf(false) }
        val chatsSelected by remember(selectedChatCount > 0) {
            mutableStateOf(selectedChatCount > 0)
        }
        BackHandler(chatsSelected || searchEnabled) {
            if (searchEnabled) {
                onClearSearch()
                searchEnabled = false
            } else if (chatsSelected) onClearSelectedChats()
        }

        JetxScaffold(
            topBar = {
                if (searchEnabled) {
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    SearchTextField(
                        inputText = searchQuery,
                        onInputTextChange = onSearchQueryChange,
                        onSearchDisable = {
                            onClearSearch()
                            searchEnabled = false
                        },
                        onSearch = onSearch,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                } else {
                    ChatListTopAppBar(
                        chatsSelected = chatsSelected,
                        selectedChatCount = selectedChatCount,
                        showDropdownMenu = showDropdownMenu,
                        onDropdownIconClick = { showDropdownMenu = true },
                        onDismissDropdownMenu = { showDropdownMenu = false },
                        onDeleteChatClick = onDeleteChatClick,
                        onClearSelectedChats = onClearSelectedChats,
                        onArchiveClick = onArchiveClick,
                        onStarredMessagesClick = onStarredMessagesClick,
                        onSettingsClick = onSettingsClick,
                        onSearchButtonClick = { searchEnabled = true }
                    )
                }
            }
        ) {
            if (searchResults.isNotEmpty()) {
                val focusManager = LocalFocusManager.current
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = searchResults,
                        key = { it.id }
                    ) {
                        UserListItem(
                            profilePictureUrl = null,
                            username = it.username,
                            supportingText = it.displayName,
                            modifier = Modifier.noRippleClickable {
                                focusManager.clearFocus()
                                onProfileClick(
                                    ChatUserArgs(
                                        recipientId = it.id,
                                        username = it.username,
                                        pictureUrl = null
                                    )
                                )
                                onClearSearch()
                            }
                        )
                    }
                }
            } else {
                ChatList(
                    chatList = state.list,
                    selectedChats = selectedChats,
                    onChatClick = onChatClick,
                    onSelectChat = onSelectChat,
                    onUnselectChat = onUnselectChat,
                    listHeader = {
                        item {
                            FilterRow(
                                enabled = !chatsSelected,
                                filterOptions = filterOptions,
                                selectedFilter = selectedFilter,
                                onFilterOptionClick = onFilterOptionClick,
                                modifier = Modifier
                                    .alpha(if (chatsSelected) 0.5f else 1f)
                                    .padding(horizontal = defaultPadding)
                            )
                        }
                        if (!archivedChatEmpty) {
                            item {
                                ArchivedChatsHeader(
                                    enabled = !chatsSelected,
                                    onArchivedChatsClick = onArchivedChatsClick,
                                    modifier = Modifier
                                        .alpha(if (chatsSelected) 0.3f else 1f)
                                        .padding(horizontal = defaultPadding)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ChatListTopAppBar(
    chatsSelected: Boolean,
    selectedChatCount: Int,
    showDropdownMenu: Boolean,
    onDropdownIconClick: () -> Unit,
    onDismissDropdownMenu: () -> Unit,
    onDeleteChatClick: () -> Unit,
    onClearSelectedChats: () -> Unit,
    onArchiveClick: () -> Unit,
    onStarredMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSearchButtonClick: () -> Unit
) {
    JetxTopAppBar(
        title = {
            if (chatsSelected) Text("$selectedChatCount")
            else Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            if (chatsSelected) BackButton { onClearSelectedChats() }
        },
        actions = {
            if (chatsSelected) {
                IconButton(onClick = onArchiveClick) {
                    Icon(
                        imageVector = Icons.Filled.Archive,
                        contentDescription = stringResource(id = R.string.archive_chat)
                    )
                }
                IconButton(onClick = onDeleteChatClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete_chat)
                    )
                }
            } else {
                IconButton(onClick = onSearchButtonClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                IconButtonDropdownMenu(
                    expanded = showDropdownMenu,
                    onIconClick = onDropdownIconClick,
                    onDismissRequest = onDismissDropdownMenu,
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
        }
    )
}

@Composable
private fun FilterRow(
    enabled: Boolean,
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
                enabled = enabled,
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
fun ArchivedChatsHeader(
    enabled: Boolean,
    onArchivedChatsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onArchivedChatsClick() }
    ) {
        Box(
            modifier = Modifier.size(
                width = 50.dp,
                height = 20.dp
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = stringResource(id = R.string.archived),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = stringResource(id = R.string.archived),
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
private fun ChatsScreenPreview() {
    JetXTheme {
        ChatListScreen(
            state = ChatListState.Success(sampleChats),
            selectedChats = emptySet(),
            archivedChatEmpty = true,
            selectedFilter = FilterOption.ALL,
            searchQuery = "",
            searchResults = emptyList(),
            onChatClick = {},
            onSelectChat = {},
            onUnselectChat = {},
            onClearSelectedChats = {},
            onDeleteChatClick = {},
            onArchiveClick = {},
            onFilterOptionClick = {},
            onArchivedChatsClick = {},
            onSearchQueryChange = {},
            onSearch = {},
            onClearSearch = {},
            onStarredMessagesClick = {},
            onSettingsClick = {}
        ) {}
    }
}