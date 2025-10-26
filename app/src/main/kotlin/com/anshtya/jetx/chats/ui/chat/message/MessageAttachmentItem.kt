package com.anshtya.jetx.chats.ui.chat.message

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.core.database.model.AttachmentInfo
import com.anshtya.jetx.core.database.model.AttachmentTransferState
import com.anshtya.jetx.core.ui.noRippleClickable
import java.io.File

@Composable
fun MessageAttachmentItem(
    attachmentInfo: AttachmentInfo,
    onClick: (String) -> Unit,
    onDownloadClick: (Int) -> Unit,
    onCancelDownloadClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        if (attachmentInfo.storageLocation != null) {
            val model = attachmentInfo.storageLocation.toUri()
            when (attachmentInfo.type) {
                AttachmentType.IMAGE -> {
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { onClick(attachmentInfo.storageLocation) }
                    )
                }
                AttachmentType.VIDEO -> {
                    AsyncImage(
                        model = Uri.fromFile(File(attachmentInfo.storageLocation)),
                        imageLoader = ImageLoader.Builder(LocalContext.current)
                            .components {
                                add(VideoFrameDecoder.Factory())
                            }.build(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { onClick(attachmentInfo.storageLocation) }
                    )
                    IconButton(
                        onClick = { onClick(attachmentInfo.storageLocation) },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Color.DarkGray.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(140.dp)
                        )
                    }
                }
            }
        } else {
            when (attachmentInfo.transferState) {
                AttachmentTransferState.STARTED -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                    Surface(
                        onClick = { onCancelDownloadClick(attachmentInfo.id) },
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
                        onClick = { onDownloadClick(attachmentInfo.id) },
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
//                            Text(attachmentInfo.size!!)
                        }
                    }
                }
            }
        }
    }
}