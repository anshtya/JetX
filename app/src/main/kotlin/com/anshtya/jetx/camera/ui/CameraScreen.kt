package com.anshtya.jetx.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.anshtya.jetx.camera.permission.CameraPermissionItem

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

    var showCameraPermissionText by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val permission = Manifest.permission.CAMERA
                showCameraPermissionText = ContextCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showCameraPermissionText = false
        else {
            val activity = context as Activity
            if (activity.shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == false
            ) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        }
    }

    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
    ) {
        if (showCameraPermissionText) {
            CameraPermissionItem(
                onContinueClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onNotNowClick = (context as Activity)::finish
            )
        } else {
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