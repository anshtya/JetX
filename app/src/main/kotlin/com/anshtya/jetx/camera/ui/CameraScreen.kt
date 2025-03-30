package com.anshtya.jetx.camera.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun CameraScreen(
    onClick: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
    ) {
        CameraContent(
            controller = controller,
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        )
        CameraControls(
            onFlipCamera = {
                controller.cameraSelector = if (
                    controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                ) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            },
            onCaptureClick = {
                onCaptureClick(
                    context = context,
                    controller = controller,
                    afterClick = { bitmap -> onClick(bitmap) }
                )
            }
        )
    }
}

@Suppress("DEPRECATION")
private fun onCaptureClick(
    context: Context,
    controller: LifecycleCameraController,
    afterClick: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val rotation = image.imageInfo.rotationDegrees
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                val bitmapImage = image.toBitmap()
                val transformedImage = Bitmap.createBitmap(
                    bitmapImage,
                    0,
                    0,
                    bitmapImage.width,
                    bitmapImage.height,
                    matrix,
                    true
                )
                afterClick(transformedImage)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}