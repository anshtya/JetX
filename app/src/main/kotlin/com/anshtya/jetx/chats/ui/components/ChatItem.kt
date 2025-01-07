package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.DayNightPreview
import com.anshtya.jetx.common.ui.ProfilePicture

@Composable
fun ChatItem(
    chat: Chat,
    onClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(chat.id) }
            .padding(10.dp)
    ) {
        ProfilePicture(
            model = chat.profilePicture,
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = chat.message,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Text(
            text = chat.timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@DayNightPreview
@Composable
private fun ChatItemPreview() {
    ComponentPreview {
        ChatItem(
            chat = Chat(
                id = 1,
                name = "name",
                profilePicture = null,
                message = "message",
                timestamp = "",
                messageStatus = MessageStatus.SEEN
            ),
            onClick = {}
        )
    }
}