package com.anshtya.jetx.camera.ui

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

@Composable
fun CameraContent(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var focusOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var showFocusIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(focusOffset) {
        if (focusOffset == Offset.Zero) return@LaunchedEffect
        showFocusIndicator = true
        delay(2500)
        showFocusIndicator = false
    }

    Box(modifier) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }

                val scaleGestureDetector = ScaleGestureDetector(
                    context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            val currentZoomRatio =
                                controller.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                            val delta = detector.scaleFactor
                            controller.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                            return true
                        }
                    }
                )

                val tapToFocusDetector = GestureDetector(
                    context, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            val x = e.x
                            val y = e.y
                            val meteringPoint = previewView.meteringPointFactory.createPoint(x, y)
                            val meteringAction = FocusMeteringAction.Builder(meteringPoint).build()
                            controller.cameraControl?.startFocusAndMetering(meteringAction)
                            focusOffset = Offset(x, y)
                            return true
                        }
                    }
                )

                previewView.setOnTouchListener { view, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    tapToFocusDetector.onTouchEvent(event)
                    view.performClick()
                    return@setOnTouchListener true
                }

                return@AndroidView previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showFocusIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .offset { focusOffset.round() }
                .offset((-24).dp, (-24).dp)
        ) {
            Spacer(
                modifier = Modifier
                    .border(2.dp, Color.White, CircleShape)
                    .size(48.dp)
            )
        }
    }
}