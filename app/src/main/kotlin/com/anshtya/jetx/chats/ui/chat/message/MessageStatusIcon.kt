package com.anshtya.jetx.chats.ui.chat.message

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anshtya.jetx.database.model.MessageStatus

@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = when (status) {
            MessageStatus.SENDING -> Icons.Default.Schedule
            MessageStatus.SENT -> Icons.Default.Done
            MessageStatus.RECEIVED, MessageStatus.SEEN -> Icons.Default.DoneAll
        },
        contentDescription = status.name,
        tint = if (status == MessageStatus.SEEN) {
            Color.Blue.copy(alpha = 0.6f)
        } else Color.Gray,
        modifier = modifier
    )
}