package com.anshtya.jetx.chats.ui.search

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatUserArgs
import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.UserListItem
import com.anshtya.jetx.util.Constants.defaultPadding

@Composable
fun SearchRoute(
    onNavigateToChat: (ChatUserArgs) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    SearchScreen(
        searchQuery = searchQuery,
        errorMessage = errorMessage,
        searchSuggestions = searchSuggestions,
        onSearchQueryChange = viewModel::changeSearchQuery,
        onClearSearch = viewModel::clearSearch,
        onProfileClick = onNavigateToChat,
        onBackClick = onBackClick,
        onErrorShown = viewModel::errorShown
    )
}

@Composable
private fun SearchScreen(
    searchQuery: String,
    errorMessage: String?,
    searchSuggestions: List<UserProfile>,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onProfileClick: (ChatUserArgs) -> Unit,
    onBackClick: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val context = LocalContext.current
    var searchBarActive by rememberSaveable { mutableStateOf(false) }
    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(searchBarActive) {
        if (!searchBarActive) {
            onClearSearch()
        }
    }

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        onErrorShown()
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!searchBarActive && !searchBarExpanded) {
                    BackButton(onBackClick)
                }
                ProfileSearchBar(
                    searchQuery = searchQuery,
                    searchSuggestions = searchSuggestions,
                    searchBarActive = searchBarActive,
                    onSearchQueryChange = onSearchQueryChange,
                    onActiveChange = {
                        searchBarActive = it
                    },
                    onClearSearch = onClearSearch,
                    onProfileClick = onProfileClick,
                    onExpandChange = {
                        searchBarExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSearchBar(
    searchQuery: String,
    searchSuggestions: List<UserProfile>,
    searchBarActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onClearSearch: () -> Unit,
    onProfileClick: (ChatUserArgs) -> Unit,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarPadding by animateDpAsState(
        targetValue = if (searchBarActive) 0.dp else defaultPadding,
        label = stringResource(R.string.search_bar_padding),
        finishedListener = {
            if (it == 0.dp) {
                onExpandChange(true)
            } else {
                onExpandChange(false)
            }
        }
    )
    val focusManager = LocalFocusManager.current

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = if (searchBarActive) searchQuery else "",
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                expanded = searchBarActive,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(text = stringResource(id = R.string.search))
                },
                leadingIcon = {
                    if (searchBarActive) {
                        BackButton { onActiveChange(false) }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank() || searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                }
            )
        },
        expanded = searchBarActive,
        onExpandedChange = onActiveChange,
        modifier = modifier.padding(searchBarPadding)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = searchSuggestions,
                key = { it.id }
            ) {
                UserListItem(
                    profilePictureUrl = it.pictureUrl,
                    username = it.username,
                    supportingText = it.name,
                    onClick = {
                        focusManager.clearFocus()
                        onProfileClick(it.toChatUserArgs())
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    ComponentPreview {
        SearchScreen(
            searchQuery = "",
            errorMessage = null,
            searchSuggestions = emptyList(),
            onSearchQueryChange = {},
            onClearSearch = {},
            onProfileClick = {},
            onBackClick = {},
            onErrorShown = {}
        )
    }
}