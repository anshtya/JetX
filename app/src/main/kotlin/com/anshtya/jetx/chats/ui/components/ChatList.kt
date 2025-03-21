package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.common.model.Chat

@Composable
fun ChatList(
    chatList: List<Chat>,
    selectedChats: Set<Int>,
    onChatClick: (ChatUserArgs) -> Unit,
    onChatLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listHeader: LazyListScope.() -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        listHeader()
        when {
            chatList.isEmpty() -> item {
                EmptyChatsItem(Modifier.fillMaxSize())
            }

            else -> items(
                items = chatList,
                key = { it.id }
            ) {
                ChatItem(
                    chat = it,
                    selected = selectedChats.contains(it.id),
                    onClick = onChatClick,
                    onLongClick = onChatLongClick
                )
            }
        }
    }
}

@Composable
private fun EmptyChatsItem(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.no_chats),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}