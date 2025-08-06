package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.anshtya.jetx.R
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.common.ui.VideoPlayerSurface
import com.anshtya.jetx.common.ui.components.button.BackButton
import com.anshtya.jetx.common.ui.components.button.SendButton
import com.anshtya.jetx.common.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.common.ui.components.textfield.MessageInputField
import com.anshtya.jetx.common.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.util.UriUtil.getMimeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MediaPreviewRoute(
    onBackClick: () -> Unit,
    navigateToChat: () -> Unit,
    viewModel: MediaPreviewViewModel
) {
    val uiState by viewModel.mediaPreviewUiState.collectAsStateWithLifecycle()
    val currentItemIndex by viewModel.currentItemIndex.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val navigateToChat by viewModel.navigateToChat.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToChat) {
        if (navigateToChat) navigateToChat()
    }

    MediaPreviewScreen(
        uiState = uiState,
        currentItemIndex = currentItemIndex,
        errorMessage = errorMessage,
        onCurrentItemChange = viewModel::onSelectItem,
        onCurrentItemCaptionChange = viewModel::onCaptionChange,
        onSendClick = viewModel::onSendClick,
        onBackClick = onBackClick,
        onDiscardMedia = viewModel::discardMedia,
        onErrorShown = viewModel::onErrorShown
    )
}

@Composable
private fun MediaPreviewScreen(
    uiState: MediaPreviewUiState,
    currentItemIndex: Int,
    errorMessage: String?,
    onCurrentItemChange: (Int) -> Unit,
    onCurrentItemCaptionChange: (Int, String) -> Unit,
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    onDiscardMedia: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    JetxScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            JetxTopAppBar(
                title = {},
                navigationIcon = { BackButton { onBackClick() } }
            )
        },
        bottomBar = {
            if (uiState is MediaPreviewUiState.DataLoaded && uiState.data.isNotEmpty()) {
                val currentItem = uiState.data[currentItemIndex]
                var currentItemCaption by remember(currentItem) {
                    mutableStateOf(currentItem.caption)
                }

                LaunchedEffect(currentItemCaption) {
                    delay(200L)
                    onCurrentItemCaptionChange(currentItemIndex, currentItemCaption)
                }

                MediaSendRow(
                    currentItemCaption = currentItemCaption,
                    onCurrentItemCaptionChange = { currentItemCaption = it },
                    onSendClick = onSendClick,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .imePadding()
                )
            }
        }
    ) {
        val context = LocalContext.current
        when (uiState) {
            is MediaPreviewUiState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is MediaPreviewUiState.DataLoaded -> {
                val sendItems = uiState.data
                if (sendItems.isNotEmpty()) {
                    val currentUri = sendItems[currentItemIndex].uri
                    val attachmentType = currentUri.getMimeType(context)?.let {
                        AttachmentType.fromMimeType(it)
                    }!!

                    val player = remember(currentUri) {
                        if (attachmentType == AttachmentType.VIDEO) {
                            ExoPlayer.Builder(context).build().apply {
                                setMediaItem(MediaItem.fromUri(currentUri))
                                prepare()
                            }
                        } else null
                    }

                    var showDiscardMediaDialog by remember { mutableStateOf(false) }
                    if (showDiscardMediaDialog) {
                        DiscardMediaDialog(
                            onDismiss = { showDiscardMediaDialog = false },
                            onConfirmClick = {
                                player?.release()
                                onDiscardMedia()
                            }
                        )
                    }

                    BackHandler {
                        player?.pause()
                        showDiscardMediaDialog = true
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        UriPreview(
                            uri = currentUri,
                            attachmentType = attachmentType,
                            player = player,
                            modifier = Modifier.weight(1f)
                        )
                        MediaRow(
                            sendItems = sendItems,
                            onItemClick = {
                                player?.release()
                                onCurrentItemChange(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaSendRow(
    currentItemCaption: String,
    onCurrentItemCaptionChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        MessageInputField(
            inputText = currentItemCaption,
            onInputTextChange = onCurrentItemCaptionChange,
            onMessageSent = {},
            modifier = Modifier.weight(1f)
        )
        SendButton(onClick = onSendClick)
    }
}

@Composable
private fun MediaRow(
    sendItems: List<SendItem>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        sendItems.forEachIndexed { index, sendItem ->
            val context = LocalContext.current
            val attachmentType = sendItem.uri.getMimeType(context)?.let {
                AttachmentType.fromMimeType(it)
            }!!
            UriItem(
                uri = sendItem.uri,
                attachmentType = attachmentType,
                modifier = Modifier.clickable { onItemClick(index) }
            )
        }
//        UriItem(
//            uri = null,
//            modifier = Modifier.clickable {}
//        )
    }
}

@Composable
private fun UriPreview(
    uri: Uri,
    attachmentType: AttachmentType,
    modifier: Modifier = Modifier,
    player: ExoPlayer? = null
) {
    Box(modifier.fillMaxSize()) {
        when (attachmentType) {
            AttachmentType.IMAGE -> {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                )
            }

            AttachmentType.VIDEO -> {
                player?.let {
                    VideoPlayerSurface(
                        player = player,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun UriItem(
    uri: Uri,
    attachmentType: AttachmentType,
    modifier: Modifier = Modifier
) {
    when (attachmentType) {
        AttachmentType.IMAGE -> {
            AsyncImage(
                model = uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.size(60.dp)
            )
        }

        AttachmentType.VIDEO -> {
            val context = LocalContext.current
            AsyncImage(
                model = uri,
                imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(VideoFrameDecoder.Factory())
                    }.build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = modifier.size(60.dp)
            )
        }
    }
//    Box(modifier.size(60.dp)) {
//        Icon(
//            imageVector = Icons.Default.Add,
//            contentDescription = null,
//            modifier = Modifier
//                .size(40.dp)
//                .align(Alignment.Center)
//        )
//    }
}

@Composable
private fun DiscardMediaDialog(
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = R.string.discard_media))
        },
        confirmButton = {
            TextButton(onConfirmClick) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}