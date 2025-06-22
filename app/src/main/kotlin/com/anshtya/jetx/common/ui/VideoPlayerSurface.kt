package com.anshtya.jetx.common.ui

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPresentationState

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerSurface(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    val presentationState = rememberPresentationState(player)

    Box(modifier) {
        PlayerSurface(
            player = player,
            modifier = Modifier
                .resizeWithContentScale(
                    ContentScale.Fit,
                    presentationState.videoSizeDp
                )
                .noRippleClickable { if (player.isPlaying) player.pause() },
        )

        PlayPauseButton(
            player = player,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlayPauseButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberPlayPauseButtonState(player)

    AnimatedVisibility(
        visible = state.showPlay,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        IconButton(
            onClick = state::onClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.DarkGray.copy(alpha = 0.7f)),
            enabled = state.isEnabled
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )
        }
    }
}