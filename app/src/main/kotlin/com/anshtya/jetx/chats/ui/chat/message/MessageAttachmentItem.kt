package com.anshtya.jetx.chats.ui.chat.message

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.database.model.AttachmentInfo
import com.anshtya.jetx.database.model.AttachmentTransferState
import java.io.File

@Composable
fun MessageAttachmentItem(
    attachmentInfo: AttachmentInfo,
    onClick: (String) -> Unit,
    onDownloadClick: (Int) -> Unit,
    onCancelDownloadClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (attachmentInfo.type) {
        AttachmentType.IMAGE -> {
            ImageView(
                id = attachmentInfo.id,
                downloadSize = attachmentInfo.size,
                transferState = attachmentInfo.transferState,
                storageLocation = attachmentInfo.storageLocation,
                onClick = onClick,
                onDownloadClick = onDownloadClick,
                onCancelDownloadClick = onCancelDownloadClick,
                modifier = modifier
            )
        }

        AttachmentType.VIDEO -> {
            // replace with video player view
            AsyncImage(
                model = Uri.fromFile(File(attachmentInfo.storageLocation!!)),
                imageLoader = ImageLoader.Builder(LocalContext.current)
                    .components {
                        add(VideoFrameDecoder.Factory())
                    }.build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = modifier.size(60.dp)
            )
        }

        AttachmentType.DOCUMENT -> {
            TODO("implement document attachment")
        }
    }
}

@Composable
private fun ImageView(
    id: Int,
    downloadSize: String?,
    transferState: AttachmentTransferState?,
    storageLocation: String?,
    onClick: (String) -> Unit,
    onDownloadClick: (Int) -> Unit,
    onCancelDownloadClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        if (storageLocation != null) {
            val model = Uri.fromFile(File(storageLocation))
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick(model.toString()) }
            )
        } else {
            when (transferState) {
                AttachmentTransferState.STARTED -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                    Surface(
                        onClick = { onCancelDownloadClick(id) },
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.Center)
                            .padding(ProgressIndicatorDefaults.CircularStrokeWidth)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }

                else -> {
                    Surface(
                        onClick = { onDownloadClick(id) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null
                            )
                            Text(downloadSize!!)
                        }
                    }
                }
            }
        }
    }
}