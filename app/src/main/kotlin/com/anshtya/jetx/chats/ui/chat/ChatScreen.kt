package com.anshtya.jetx.chats.ui.chat

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.attachments.ui.preview.MediaPreviewActivity
import com.anshtya.jetx.camera.CameraActivity
import com.anshtya.jetx.chats.ui.chat.message.MessageItemContent
import com.anshtya.jetx.chats.ui.components.DeleteMessageDialog
import com.anshtya.jetx.chats.ui.components.ProfilePicturePopup
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.MessageInputField
import com.anshtya.jetx.common.ui.ProfilePicture
import com.anshtya.jetx.common.ui.SendButton
import com.anshtya.jetx.database.model.AttachmentInfo
import com.anshtya.jetx.database.model.MessageWithAttachment
import com.anshtya.jetx.sampledata.sampleUsers
import com.anshtya.jetx.util.Constants
import com.anshtya.jetx.util.getDateOrTime
import com.anshtya.jetx.util.isNotSameDay
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun ChatRoute(
    onBackClick: () -> Unit,
    onNavigateToMediaScreen: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatUser by viewModel.recipientUser.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val selectedMessages by viewModel.selectedMessages.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    ChatScreen(
        recipientUser = chatUser,
        chatMessages = chatMessages,
        selectedMessages = selectedMessages,
        errorMessage = errorMessage,
        onMessageSent = viewModel::sendMessage,
        onMessageSelect = viewModel::selectMessage,
        onMessageUnselect = viewModel::unselectMessage,
        onClearSelectedMessages = viewModel::clearSelectedMessages,
        onDeleteMessageClick = viewModel::deleteMessages,
        onChatSeen = viewModel::markChatMessagesAsSeen,
        onAttachmentClick = onNavigateToMediaScreen,
        onAttachmentDownloadClick = viewModel::downloadAttachment,
        onCancelDownloadClick = viewModel::cancelAttachmentDownload,
        onErrorShown = viewModel::errorShown,
        onBackClick = onBackClick
    )
}

@Composable
private fun ChatScreen(
    recipientUser: RecipientUser?,
    chatMessages: List<MessageWithAttachment>,
    selectedMessages: Set<Int>,
    errorMessage: String?,
    onMessageSent: (String) -> Unit,
    onMessageSelect: (Int) -> Unit,
    onMessageUnselect: (Int) -> Unit,
    onClearSelectedMessages: () -> Unit,
    onDeleteMessageClick: () -> Unit,
    onChatSeen: () -> Unit,
    onAttachmentClick: (String) -> Unit,
    onAttachmentDownloadClick: (Int, Int) -> Unit,
    onCancelDownloadClick: (Int, Int) -> Unit,
    onErrorShown: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages) {
        listState.scrollToItem(0)
        if (chatMessages.isNotEmpty()) {
            val hasUnseenMessages = chatMessages.any {
                it.messageInfo.senderId == recipientUser?.id && it.messageInfo.status == MessageStatus.RECEIVED
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

    var showProfileViewPopup by remember { mutableStateOf(false) }
    if (showProfileViewPopup) {
        ProfilePicturePopup(
            picture = recipientUser?.pictureUrl,
            onDismiss = { showProfileViewPopup = false }
        )
    }

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        onErrorShown()
    }

    Scaffold(
        topBar = {
            ChatTopAppBar(
                selectedMessagesCount = selectedMessagesCount,
                recipientUser = recipientUser,
                onProfilePictureClick = { showProfileViewPopup = true },
                onBackClick = onBackClick,
                onClearSelectedMessages = onClearSelectedMessages,
                onDeleteClick = { showDeleteMessageDialog = true },
                onStarClick = {}
            )
        },
        bottomBar = {
            ChatTextInput(
                recipientUserId = recipientUser?.id,
                onMessageSent = onMessageSent,
                modifier = Modifier.padding(8.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 2.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            chatMessages.forEachIndexed { index, message ->
                val messageInfo = message.messageInfo

                val previousIndex = (index - 1).takeIf { it >= 0 }
                val previousMessageInfo = previousIndex?.let { chatMessages[it].messageInfo }
                if (previousMessageInfo != null &&
                    isNotSameDay(messageInfo.createdAt, previousMessageInfo.createdAt)
                ) {
                    item(key = previousMessageInfo.createdAt.toLocalDate()) {
                        DateHeader(
                            date = previousMessageInfo.createdAt.getDateOrTime(getToday = true),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                val authorChanged = previousMessageInfo != null &&
                        previousMessageInfo.senderId != messageInfo.senderId
                val isAuthor = messageInfo.senderId != recipientUser?.id

                item(key = messageInfo.id) {
                    MessageItem(
                        id = messageInfo.id,
                        text = messageInfo.text,
                        time = messageInfo.createdAt.getDateOrTime(getTimeOnly = true),
                        status = messageInfo.status,
                        isAuthor = isAuthor,
                        messagesSelected = messagesSelected,
                        isSelected = selectedMessages.any { it == messageInfo.id },
                        attachmentInfo = message.attachment,
                        onSelect = onMessageSelect,
                        onUnselect = onMessageUnselect,
                        onAttachmentClick = onAttachmentClick,
                        onAttachmentDownloadClick = onAttachmentDownloadClick,
                        onCancelDownloadClick = onCancelDownloadClick,
                        modifier = Modifier
                            .padding(
                                top = 2.dp,
                                bottom = if (authorChanged) 8.dp else 2.dp
                            )
                    )
                }

                if (index == chatMessages.size - 1) {
                    item(key = messageInfo.createdAt.toLocalDate()) {
                        DateHeader(
                            date = messageInfo.createdAt.getDateOrTime(getToday = true),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
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
    onProfilePictureClick: () -> Unit,
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
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onProfilePictureClick() }
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
private fun ChatTextInput(
    recipientUserId: UUID?,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val intent = Intent(context, MediaPreviewActivity::class.java).apply {
                putExtra(Constants.RECIPIENT_INTENT_KEY, recipientUserId)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            }
            context.startActivity(intent)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        MessageInputField(
            inputText = inputText,
            onInputTextChange = { inputText = it },
            onMessageSent = onMessageSent,
            modifier = Modifier.weight(1f),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = {
                            pickMediaLauncher.launch(
                                input = PickVisualMediaRequest(PickVisualMedia.ImageAndVideo)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(context, CameraActivity::class.java).apply {
                                putExtra(Constants.RECIPIENT_INTENT_KEY, recipientUserId)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        SendButton(
            onClick = {
                if (inputText.isNotBlank()) {
                    onMessageSent(inputText)
                    inputText = ""
                }
            }
        )
    }
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
    text: String?,
    time: String,
    status: MessageStatus,
    isAuthor: Boolean,
    messagesSelected: Boolean,
    isSelected: Boolean,
    attachmentInfo: AttachmentInfo?,
    onSelect: (Int) -> Unit,
    onUnselect: (Int) -> Unit,
    onAttachmentClick: (String) -> Unit,
    onAttachmentDownloadClick: (Int, Int) -> Unit,
    onCancelDownloadClick: (Int, Int) -> Unit,
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
                text = text,
                time = time,
                status = if (isAuthor) status else null,
                attachmentInfo = attachmentInfo,
                onAttachmentClick = onAttachmentClick,
                onAttachmentDownloadClick = { onAttachmentDownloadClick(it, id) },
                onCancelDownloadClick = { onCancelDownloadClick(it, id) },
                modifier = Modifier.padding(8.dp)
            )
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
            chatMessages = emptyList(),
            selectedMessages = emptySet(),
            errorMessage = null,
            onMessageSent = {},
            onMessageSelect = {},
            onMessageUnselect = {},
            onClearSelectedMessages = {},
            onDeleteMessageClick = {},
            onAttachmentDownloadClick = { _, _ -> },
            onCancelDownloadClick = { _, _ -> },
            onChatSeen = {},
            onAttachmentClick = {},
            onErrorShown = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun MessageItemPreview() {
    ComponentPreview {
        MessageItem(
            id = 1,
            text = "text",
            time = ZonedDateTime.now().getDateOrTime(getTimeOnly = true),
            status = MessageStatus.SENT,
            isAuthor = true,
            messagesSelected = false,
            isSelected = false,
            attachmentInfo = null,
            onSelect = {},
            onUnselect = {},
            onAttachmentClick = {},
            onAttachmentDownloadClick = { _, _ -> },
            onCancelDownloadClick = { _, _ -> }
        )
    }
}