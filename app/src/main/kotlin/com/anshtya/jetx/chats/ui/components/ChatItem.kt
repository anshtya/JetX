package com.anshtya.jetx.chats.ui.components

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.message.MessageStatusIcon
import com.anshtya.jetx.chats.ui.chat.toChatUserArgs
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.DayNightPreview
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.sampledata.sampleChats
import com.anshtya.jetx.util.Constants.defaultPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    chat: Chat,
    selected: Boolean,
    onClick: (ChatUserArgs) -> Unit,
    onProfileViewClick: (String?) -> Unit,
    onLongClick: (Int) -> Unit,
    onUnselectChat: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else Color.Unspecified,
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                onClick = {
                    if (selected)
                        onUnselectChat(chat.id)
                    else
                        onClick(chat.toChatUserArgs())
                },
                onLongClick = { onLongClick(chat.id) }
            )
            .padding(
                horizontal = defaultPadding,
                vertical = 4.dp
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable { onProfileViewClick(chat.profilePicture) }
        ) {
            ProfilePicture(
                model = chat.profilePicture,
                modifier = Modifier.size(50.dp)
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn(animationSpec = spring(stiffness = 1000f)),
                exit = scaleOut(animationSpec = spring(stiffness = 1000f)),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = Color.White,
                    contentDescription = stringResource(id = R.string.chat_selected),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(2.dp)
                        .size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
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
                    text = chat.timestamp!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                )
            }
            Spacer(Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (chat.isSender) {
                    MessageStatusIcon(
                        status = chat.messageStatus!!,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text(
                    text = chat.message!!,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unreadCount > 0) {
                    Spacer(Modifier.width(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box {
                            Text(
                                text = "${chat.unreadCount}",
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@DayNightPreview
@Composable
private fun ChatItemPreview() {
    ComponentPreview {
        ChatItem(
            chat = sampleChats.first(),
            selected = false,
            onClick = {},
            onProfileViewClick = {},
            onLongClick = {},
            onUnselectChat = {}
        )
    }
}