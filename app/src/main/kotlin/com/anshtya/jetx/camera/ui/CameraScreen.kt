package com.anshtya.jetx.camera.ui

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.camera.permission.CameraPermissionRequest
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.util.FileUtil
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onNavigateToPreview: (Uri) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val navigateToPreview by viewModel.navigateToPreview.collectAsStateWithLifecycle()
    LaunchedEffect(navigateToPreview) {
        if (navigateToPreview) {
            onNavigateToPreview(viewModel.capturedMediaUri!!)
            viewModel.onNavigate()
        }
    }

    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    errorMessage?.let {
        scope.launch { snackbarHostState.showSnackbar(it) }
        viewModel.onErrorShown()
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val requiredPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    var permissionsChecked by remember { mutableStateOf(false) }
    var permissionsRequested by remember { mutableStateOf(false) }
    var grantedPermissions by remember { mutableStateOf(setOf<String>()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        grantedPermissions = permissions.filterValues { it }.keys
        permissionsRequested = true
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                grantedPermissions = requiredPermissions.filter { permission ->
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                }.toSet()
                permissionsChecked = true
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notGranted = remember(grantedPermissions) {
        requiredPermissions.filterNot { grantedPermissions.contains(it) }
    }

    JetxScaffold {
        if (permissionsChecked) {
            if (notGranted.isEmpty()) {
                CameraLayout(
                    onImageCapture = viewModel::onImageCapture,
                    onVideoCapture = viewModel::onVideoCapture,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CameraPermissionRequest(
                    permissionsDenied = permissionsRequested,
                    deniedPermissions = notGranted,
                    onGoToAppInfo = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    onPermissionRequest = { launcher.launch(requiredPermissions.toTypedArray()) },
                    onNotNowClick = onBackClick
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun CameraLayout(
    onImageCapture: (Bitmap) -> Unit,
    onVideoCapture: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val controller = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
            videoCaptureQualitySelector = QualitySelector.fromOrderedList(
                listOf(Quality.HD, Quality.SD)
            )
        }
    }

    var recordingStarted by remember { mutableStateOf(false) }
    var recording: Recording? = remember { null }

    Column(modifier.fillMaxSize()) {
        CameraContent(
            controller = controller,
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        )
        CameraControls(
            recordingStarted = recordingStarted,
            onFlipCamera = {
                controller.cameraSelector = if (
                    controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                ) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            },
            onPictureClick = {
                onPictureClick(
                    context = context,
                    controller = controller,
                    afterClick = onImageCapture
                )
            },
            onStartRecording = {
                recording = onVideoCapture(
                    context = context,
                    controller = controller,
                    onStart = { recordingStarted = true },
                    onCapture = onVideoCapture
                )
            },
            onStopRecording = {
                recording?.let {
                    it.stop()
                    recordingStarted = false
                }
            }
        )
    }
}

private fun onPictureClick(
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
                Log.e("CameraScreen", "Couldn't take photo: ", exception)
            }
        }
    )
}

@SuppressLint("MissingPermission")
private fun onVideoCapture(
    context: Context,
    controller: LifecycleCameraController,
    onStart: () -> Unit,
    onCapture: (Uri) -> Unit
): Recording {
    val outputPath = FileUtil.createFile(
        filePath = FileUtil.getAttachmentCacheDirectory(context), ext = "mp4"
    )
    return controller.startRecording(
        FileOutputOptions
            .Builder(outputPath).setDurationLimitMillis(60000L).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context)
    ) { videoRecordEvent ->
        when (videoRecordEvent) {
            is VideoRecordEvent.Start -> onStart()

            is VideoRecordEvent.Finalize -> {
                if (!videoRecordEvent.hasError()) {
                    val recordingUri = videoRecordEvent.outputResults.outputUri
                    onCapture(recordingUri)
                }
            }

            else -> {}
        }
    }
}