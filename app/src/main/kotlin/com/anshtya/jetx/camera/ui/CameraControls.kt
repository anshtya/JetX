package com.anshtya.jetx.camera.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R

@Composable
fun CameraControls(
    recordingStarted: Boolean,
    onFlipCamera: () -> Unit,
    onPictureClick: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth(0.76f)
            .height(140.dp)
    ) {
        FlipCameraButton(
            onClick = {
                if (!recordingStarted) onFlipCamera()
            }
        )
        CaptureButton(
            recordingStarted = recordingStarted,
            onClick = onPictureClick,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )
    }
}

@Composable
private fun FlipCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.size(40.dp),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.FlipCameraAndroid,
            contentDescription = stringResource(id = R.string.flip_camera),
            tint = Color.White,
            modifier = Modifier.size(72.dp)
        )
    }
}

@Composable
private fun CaptureButton(
    recordingStarted: Boolean,
    onClick: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
//    val animatedProgress by animateFloatAsState(
//        targetValue = if (recordingStarted) 1f else 0f,
//        animationSpec = tween(durationMillis = 60000),
//        label = ""
//    )


    val recordingAnimatable = remember { Animatable(0f) }
    LaunchedEffect(recordingStarted) {
        if (recordingStarted) {
            recordingAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 60000)
            )
        } else {
            recordingAnimatable.stop()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(80.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onStartRecording() },
                    onTap = { onClick() },
                    onPress = {
                        awaitRelease()
                        onStopRecording()
                    }
                )
            }
            .drawBehind {
                val borderWidth = 4.dp.toPx()
                if (!recordingStarted) {
                    drawCircle(
                        color = Color.White,
                        style = Stroke(width = borderWidth)
                    )
                } else {
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = 360 * recordingAnimatable.value,
                        useCenter = false,
                        style = Stroke(
                            width = borderWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(if (recordingStarted) 40.dp else 60.dp)
                .clip(CircleShape)
                .background(if (recordingStarted) Color.Red else Color.White)
        )
    }
}