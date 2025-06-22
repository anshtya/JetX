package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.anshtya.jetx.attachments.AttachmentType
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.SendButton
import com.anshtya.jetx.common.ui.VideoPlayerSurface
import com.anshtya.jetx.util.UriUtil.getMimeType
import kotlinx.coroutines.launch

@Composable
fun MediaPreviewRoute(
    navigateToChat: () -> Unit,
    viewModel: MediaPreviewViewModel
) {
    val uris by viewModel.uris.collectAsStateWithLifecycle()
    val currentUri by viewModel.currentUri.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val navigateToChat by viewModel.navigateToChat.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToChat) {
        if (navigateToChat) navigateToChat()
    }

    MediaPreviewScreen(
        uris = uris,
        currentUri = currentUri,
        errorMessage = errorMessage,
        onSend = viewModel::onSend,
        onErrorShown = viewModel::onErrorShown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaPreviewScreen(
    uris: Map<Uri, String>,
    currentUri: Uri,
    errorMessage: String?,
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
                navigationIcon = { BackButton { } }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {},
                floatingActionButton = { SendButton(onClick = onSend) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                UriPreview(
                    uri = currentUri,
                    modifier = Modifier.weight(1f)
                )
                MediaRow(uris = uris.keys.toList())
            }
        }
    }
}

@Composable
private fun MediaRow(
    uris: List<Uri>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        uris.forEach { uri ->
            UriItem(uri)
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
        val attachmentType = AttachmentType.Companion.fromMimeType(uri.getMimeType(context)!!)
        when (attachmentType) {
            AttachmentType.IMAGE -> {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
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

            else -> {}
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
        val attachmentType = AttachmentType.Companion.fromMimeType(uri.getMimeType(context)!!)
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

            else -> {}
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