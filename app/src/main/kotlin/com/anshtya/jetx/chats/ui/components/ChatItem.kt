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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshtya.jetx.chats.data.Chat
import com.anshtya.jetx.chats.data.fake.fakeChats
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.DayNightPreview

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
        Surface(
            shape = CircleShape,
            color = Color.Red,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterVertically)
        ) {}

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
            text = chat.timeStamp,
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
            chat = fakeChats.first(),
            onClick = {}
        )
    }
}