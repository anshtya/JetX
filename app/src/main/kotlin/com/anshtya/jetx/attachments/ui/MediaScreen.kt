package com.anshtya.jetx.attachments.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.common.ui.VideoPlayerSurface
import com.anshtya.jetx.common.ui.components.button.BackButton
import com.anshtya.jetx.common.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.common.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.util.UriUtil.getMimeType

@Composable
fun MediaScreen(
    data: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            val uri = data.toUri()
            val attachmentType = AttachmentType.fromMimeType(uri.getMimeType(context)!!)!!
            when (attachmentType) {
                AttachmentType.IMAGE -> {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }
                AttachmentType.VIDEO -> {
                    val player = remember {
                        ExoPlayer.Builder(context).build().apply {
                            setMediaItem(MediaItem.fromUri(uri))
                            prepare()
                            play()
                        }
                    }
                    BackHandler {
                        player.release()
                        onBackClick()
                    }
                    VideoPlayerSurface(
                        player = player,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}