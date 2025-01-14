package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.util.Constants

@Composable
fun ChatList(
    chatList: List<Chat>,
    onChatClick: (ChatUserArgs) -> Unit,
    onChatLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    slot: LazyListScope.() -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = Constants.defaultPadding
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        slot()
        items(
            items = chatList,
            key = { it.id }
        ) {
            ChatItem(
                chat = it,
                onClick = onChatClick,
                onLongClick = onChatLongClick
            )
        }
    }
}