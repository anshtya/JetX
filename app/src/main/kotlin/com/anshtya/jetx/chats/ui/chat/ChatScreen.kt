package com.anshtya.jetx.chats.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.StarBorder
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
import com.anshtya.jetx.chats.ui.components.DeleteMessageDialog
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.common.ui.message.MessageDetails
import com.anshtya.jetx.common.ui.message.MessageItemContent
import com.anshtya.jetx.sampledata.sampleChatMessages
import com.anshtya.jetx.sampledata.sampleUsers

@Composable
fun ChatRoute(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatUser by viewModel.recipientUser.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val selectedMessages by viewModel.selectedMessages.collectAsStateWithLifecycle()

    ChatScreen(
        recipientUser = chatUser,
        chatMessages = chatMessages,
        selectedMessages = selectedMessages,
        onMessageSent = viewModel::sendMessage,
        onMessageSelect = viewModel::selectMessage,
        onMessageUnselect = viewModel::unselectMessage,
        onClearSelectedMessages = viewModel::clearSelectedMessages,
        onDeleteMessageClick = viewModel::deleteMessages,
        onChatSeen = viewModel::markChatMessagesAsSeen,
        onBackClick = onBackClick
    )
}

@Composable
private fun ChatScreen(
    recipientUser: RecipientUser?,
    chatMessages: DateChatMessages,
    selectedMessages: Set<Int>,
    onMessageSent: (String) -> Unit,
    onMessageSelect: (Int) -> Unit,
    onMessageUnselect: (Int) -> Unit,
    onClearSelectedMessages: () -> Unit,
    onDeleteMessageClick: () -> Unit,
    onChatSeen: () -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages) {
        listState.scrollToItem(0)
        if (chatMessages.messages.isNotEmpty()) {
            val hasUnseenMessages = chatMessages.messages.any { (_, messages) ->
                messages.any {
                    it.senderId == recipientUser?.id && it.status == MessageStatus.RECEIVED
                }
            }
            if (hasUnseenMessages) {
                onChatSeen()
            }
        }
    }

    val selectedMessagesCount = remember(selectedMessages) { selectedMessages.size }
    val messagesSelected by remember(selectedMessagesCount > 0) {
        mutableStateOf(selectedMessagesCount > 0)
    }

    BackHandler(messagesSelected) {
        onClearSelectedMessages()
    }

    var showDeleteMessageDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteMessageDialog) {
        DeleteMessageDialog(
            messageCount = selectedMessagesCount,
            onDismissRequest = { showDeleteMessageDialog = false },
            onConfirmClick = {
                onDeleteMessageClick()
                showDeleteMessageDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            ChatTopAppBar(
                selectedMessagesCount = selectedMessagesCount,
                recipientUser = recipientUser,
                onBackClick = onBackClick,
                onClearSelectedMessages = onClearSelectedMessages,
                onDeleteClick = { showDeleteMessageDialog = true },
                onStarClick = {}
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
            contentPadding = PaddingValues(vertical = 2.dp),
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
                        id = message.id,
                        text = message.text,
                        time = message.createdAt,
                        status = message.status,
                        isAuthor = isAuthor,
                        messagesSelected = messagesSelected,
                        isSelected = selectedMessages.any { it == message.id },
                        onSelect = onMessageSelect,
                        onUnselect = onMessageUnselect,
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
    selectedMessagesCount: Int,
    recipientUser: RecipientUser?,
    onBackClick: () -> Unit,
    onClearSelectedMessages: () -> Unit,
    onDeleteClick: () -> Unit,
    onStarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messageSelected = remember(selectedMessagesCount) { selectedMessagesCount > 0 }

    TopAppBar(
        title = {
            if (messageSelected) Text("$selectedMessagesCount")
            else Row(
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
            BackButton { if (messageSelected) onClearSelectedMessages() else onBackClick() }
        },
        actions = {
            if (messageSelected) {
                IconButton(
                    onClick = onStarClick,
                ) {
                    Icon(
                        imageVector = Icons.Filled.StarBorder,
                        contentDescription = stringResource(id = R.string.star_message)
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageItem(
    id: Int,
    text: String,
    time: String,
    status: MessageStatus,
    isAuthor: Boolean,
    messagesSelected: Boolean,
    isSelected: Boolean,
    onSelect: (Int) -> Unit,
    onUnselect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = if (isAuthor) Arrangement.End else Arrangement.Start,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                } else Color.Transparent,
            )
            .combinedClickable(
                onClick = {
                    if (isSelected && messagesSelected) {
                        onUnselect(id)
                    } else if (messagesSelected) {
                        onSelect(id)
                    }
                },
                onLongClick = { onSelect(id) }
            )
            .padding(horizontal = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = if (isAuthor) 8.dp else 0.dp,
                bottomEnd = if (isAuthor) 0.dp else 8.dp
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            } else if (isAuthor) {
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
            recipientUser = RecipientUser(
                id = user.id,
                username = user.username,
                pictureUrl = user.pictureUrl
            ),
            chatMessages = DateChatMessages(emptyMap()),
            selectedMessages = emptySet(),
            onMessageSent = {},
            onMessageSelect = {},
            onMessageUnselect = {},
            onClearSelectedMessages = {},
            onDeleteMessageClick = {},
            onChatSeen = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun MessageItemPreview() {
    ComponentPreview {
        val message = sampleChatMessages.first()
        MessageItem(
            id = 1,
            text = message.text,
            time = message.createdAt,
            status = message.status,
            isAuthor = true,
            messagesSelected = false,
            isSelected = false,
            onSelect = {},
            onUnselect = {}
        )
    }
}