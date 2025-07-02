package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.MessageInputField
import com.anshtya.jetx.common.ui.SendButton
import com.anshtya.jetx.common.ui.VideoPlayerSurface
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
        onSend = viewModel::onSend,
        onBackClick = onBackClick,
        onErrorShown = viewModel::onErrorShown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaPreviewScreen(
    uiState: MediaPreviewUiState,
    currentItemIndex: Int,
    errorMessage: String?,
    onCurrentItemChange: (Int) -> Unit,
    onCurrentItemCaptionChange: (Int, String) -> Unit,
    onBackClick: () -> Unit,
    onSend: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        onErrorShown()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MessageInputField(
                        inputText = currentItemCaption,
                        onInputTextChange = { currentItemCaption = it },
                        onMessageSent = {},
                        modifier = Modifier.weight(1f)
                    )
                    SendButton(onClick = onSend)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is MediaPreviewUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is MediaPreviewUiState.DataLoaded -> {
                    val sendItems = uiState.data
                    if (sendItems.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        ) {
                            UriPreview(
                                uri = sendItems[currentItemIndex].uri,
                                modifier = Modifier.weight(1f)
                            )
                            MediaRow(
                                sendItems = sendItems,
                                onItemClick = onCurrentItemChange
                            )
                        }
                    }
                }
            }
        }
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
            UriItem(
                uri = sendItem.uri,
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(modifier.fillMaxSize()) {
        val attachmentType = uri.getMimeType(context)?.let { AttachmentType.fromMimeType(it) }
            ?: AttachmentType.DOCUMENT
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
                val player = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(uri))
                        prepare()
                    }
                }
                VideoPlayerSurface(
                    player = player,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                )
            }

            AttachmentType.DOCUMENT -> {}
        }
    }
}

@Composable
private fun UriItem(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    uri?.let { uri ->
        val attachmentType = uri.getMimeType(context)?.let { AttachmentType.fromMimeType(it) }
            ?: AttachmentType.DOCUMENT
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

            AttachmentType.DOCUMENT -> {}
        }
    } ?: Box(modifier.size(60.dp)) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
        )
    }
}