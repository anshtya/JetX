package com.anshtya.jetx.chats.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.anshtya.jetx.common.ui.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScaffold(
    selectedChatCount: Int,
    onClearSelectedChats: () -> Unit,
    modifier: Modifier = Modifier,
    topBarTitle: @Composable () -> Unit = {},
    topBarNavigationIcon: @Composable () -> Unit = {},
    topBarActions: @Composable (RowScope.(chatsSelected: Boolean) -> Unit) = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues, chatsSelected: Boolean) -> Unit
) {
    val chatsSelected by remember(selectedChatCount > 0) {
        mutableStateOf(selectedChatCount > 0)
    }

    BackHandler(chatsSelected) {
        onClearSelectedChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (chatsSelected) {
                        Text("$selectedChatCount")
                    } else topBarTitle()
                },
                navigationIcon = {
                    if (chatsSelected) {
                        BackButton { onClearSelectedChats() }
                    } else topBarNavigationIcon()
                },
                actions = { topBarActions(chatsSelected) },
            )
        },
        floatingActionButton = floatingActionButton,
        modifier = modifier,
        content = { content(it, chatsSelected) }
    )
}