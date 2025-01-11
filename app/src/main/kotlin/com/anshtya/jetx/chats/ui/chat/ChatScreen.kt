package com.anshtya.jetx.chats.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.IconButtonDropdownMenu
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.common.util.Constants
import com.anshtya.jetx.common.util.FULL_DATE
import com.anshtya.jetx.common.util.getDateOrTime
import java.util.UUID

@Composable
fun ChatRoute(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatUser by viewModel.recipientUser.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    ChatScreen(
        recipientUser = chatUser,
        chatMessages = chatMessages,
        onMessageSent = viewModel::sendMessage,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatScreen(
    recipientUser: RecipientUser?,
    chatMessages: DateChatMessages,
    onMessageSent: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ChatTopAppBar(
                recipientUser = recipientUser,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = Constants.defaultPadding,
                    end = Constants.defaultPadding,
                    bottom = Constants.defaultPadding
                )
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                chatMessages.messages.forEach { (date, messages) ->
                    stickyHeader {
                        DateHeader(
                            date = date.getDateOrTime(
                                datePattern = FULL_DATE,
                                getDateOnly = true
                            ),
                        )
                    }
                    items(
                        items = messages,
                        key = { it.id }
                    ) {

                        val isSender = it.senderId != recipientUser?.id
                        MessageItem(
                            text = it.text,
                            time = it.createdAt.getDateOrTime(getTimeOnly = true),
                            status = it.status,
                            isSender = isSender
                        )
                    }
                }
            }
            ChatInput(
                onMessageSent = onMessageSent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopAppBar(
    recipientUser: RecipientUser?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfilePicture(
                    model = recipientUser?.pictureUrl,
                    onClick = {},
                    modifier = Modifier.size(40.dp)
                )
                recipientUser?.username?.let {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            BackButton { onBackClick() }
        },
        actions = {
            IconButtonDropdownMenu(
                expanded = showDropdownMenu,
                onIconClick = { showDropdownMenu = !showDropdownMenu },
                onDismissRequest = { showDropdownMenu = false },
            ) {
                // TODO: add menu items
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ChatInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by rememberSaveable { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        ChatInputField(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onMessageSent = onMessageSent,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                if (inputText.isNotBlank()) {
                    onMessageSent(inputText)
                    inputText = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(id = R.string.send_message)
            )
        }
    }
}

@Composable
private fun ChatInputField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = inputText,
        onValueChange = onInputTextChange,
        placeholder = {
            Text(text = stringResource(id = R.string.chatinputfield_placeholder))
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions {
            if (inputText.isNotBlank()) {
                onMessageSent(inputText)
                onInputTextChange("")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun DateHeader(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Text(date)
        }
    }
}

@Composable
private fun MessageItem(
    text: String,
    time: String,
    status: MessageStatus,
    isSender: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (isSender) 8.dp else 0.dp,
                bottomEnd = if (isSender) 0.dp else 8.dp
            ),
            color = if (isSender) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.tertiary
            },
            modifier = Modifier
                .sizeIn(
                    minWidth = 80.dp,
                    maxWidth = 250.dp
                )

        ) {
            Box(Modifier.padding(10.dp)) {
                Text(text = text)
                Text(
                    text = time,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    ComponentPreview {
        ChatScreen(
            onBackClick = {},
            chatMessages = DateChatMessages(emptyMap()),
            recipientUser = RecipientUser(
                id = UUID.fromString("hi"),
                username = "user",
                pictureUrl = null
            ),
            onMessageSent = {}
        )
    }
}

@Preview
@Composable
private fun MessageItemPreview() {
    ComponentPreview {
        MessageItem(
            text = "hello",
            time = "10:00 am",
            status = MessageStatus.SEEN,
            isSender = true
        )
    }
}