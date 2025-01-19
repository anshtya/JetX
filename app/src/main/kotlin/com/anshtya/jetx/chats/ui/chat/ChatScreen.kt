package com.anshtya.jetx.chats.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import com.anshtya.jetx.common.ui.message.MessageDetails
import com.anshtya.jetx.common.ui.message.MessageItemContent
import com.anshtya.jetx.sampledata.sampleChatMessages
import com.anshtya.jetx.sampledata.sampleUsers
import com.anshtya.jetx.util.Constants.defaultPadding
import java.util.UUID

@Composable
fun ChatRoute(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatUser by viewModel.recipientUser.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    LaunchedEffect(chatMessages.messages.isNotEmpty()) {
        if (chatMessages.messages.isNotEmpty()) {
            viewModel.markChatSeen()
        }
    }

    ChatScreen(
        recipientUser = chatUser,
        chatMessages = chatMessages,
        onMessageSent = viewModel::sendMessage,
        onMessageSeen = viewModel::markMessageSeen,
        onBackClick = onBackClick
    )
}

@Composable
private fun ChatScreen(
    recipientUser: RecipientUser?,
    chatMessages: DateChatMessages,
    onMessageSent: (String) -> Unit,
    onMessageSeen: (UUID) -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            ChatTopAppBar(
                recipientUser = recipientUser,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ChatInput(onMessageSent = onMessageSent)
        }
    ) { paddingValues ->
        var isAuthorState: Boolean? = remember { false }

        LazyColumn(
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = defaultPadding, vertical = 2.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            chatMessages.messages.forEach { (date, messages) ->
                itemsIndexed(
                    items = messages,
                    key = { _, message -> message.id }
                ) { index, message ->
                    val isAuthor = message.senderId != recipientUser?.id
                    val bottomPadding = if (isAuthorState != isAuthor && index > 0) {
                        isAuthorState = isAuthor
                        4.dp
                    } else 0.dp

                    MessageItem(
                        text = message.text,
                        time = message.createdAt,
                        status = message.status,
                        isAuthor = isAuthor,
                        onMessageSeen = { onMessageSeen(message.id) },
                        modifier = Modifier.padding(top = 2.dp, bottom = bottomPadding)
                    )
                }
                item(key = date) {
                    DateHeader(date = date)
                }
            }
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
            .heightIn(56.dp)
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
            capitalization = KeyboardCapitalization.Sentences,
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
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    text: String,
    time: String,
    status: MessageStatus,
    isAuthor: Boolean,
    onMessageSeen: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        if (!isAuthor && status == MessageStatus.RECEIVED) onMessageSeen()
    }

    Row(
        horizontalArrangement = if (isAuthor) Arrangement.End else Arrangement.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (isAuthor) 8.dp else 0.dp,
                bottomEnd = if (isAuthor) 0.dp else 8.dp
            ),
            color = if (isAuthor) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        ) {
            MessageItemContent(
                message = text,
                modifier = Modifier
                    .sizeIn(maxWidth = 250.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                MessageDetails(
                    time = time,
                    status = if (isAuthor) status else null
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    ComponentPreview {
        val user = sampleUsers.first()
        ChatScreen(
            onBackClick = {},
            chatMessages = DateChatMessages(emptyMap()),
            recipientUser = RecipientUser(
                id = user.id,
                username = user.username,
                pictureUrl = user.pictureUrl
            ),
            onMessageSent = {},
            onMessageSeen = {},
        )
    }
}

@Preview
@Composable
private fun MessageItemPreview() {
    ComponentPreview {
        val message = sampleChatMessages.first()
        MessageItem(
            text = message.text,
            time = message.createdAt,
            status = message.status,
            isAuthor = true,
            onMessageSeen = {}
        )
    }
}