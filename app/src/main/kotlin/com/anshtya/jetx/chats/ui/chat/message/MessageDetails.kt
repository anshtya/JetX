package com.anshtya.jetx.chats.ui.chat.message

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.common.model.MessageStatus

@Composable
fun MessageDetails(
    time: String,
    modifier: Modifier = Modifier,
    status: MessageStatus? = null,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        status?.let {
            Spacer(Modifier.width(2.dp))
            MessageStatusIcon(
                status = status,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}