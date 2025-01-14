package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatUserArgs
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.DayNightPreview
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.common.util.getDateOrTime
import java.time.ZonedDateTime
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    chat: Chat,
    onClick: (ChatUserArgs) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(chat.toChatUserArgs()) },
                onLongClick = {
                    // TODO: add long click support
                }
            )
    ) {
        ProfilePicture(
            model = chat.profilePicture,
            onClick = {
                // TODO: add profile view
            },
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.username,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = chat.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = chat.message,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@DayNightPreview
@Composable
private fun ChatItemPreview() {
    ComponentPreview {
        ChatItem(
            chat = Chat(
                id = 1,
                recipientId = UUID.fromString("hi"),
                username = "name",
                profilePicture = null,
                message = "message",
                timestamp = ZonedDateTime.now().getDateOrTime(),
                messageStatus = MessageStatus.SEEN
            ),
            onClick = {},
            onLongClick = {}
        )
    }
}