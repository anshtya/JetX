package com.anshtya.jetx.camera

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anshtya.jetx.camera.ui.CameraScreen
import com.anshtya.jetx.camera.ui.CapturePreviewScreen
import kotlinx.serialization.Serializable

private sealed interface CameraDestination {
    @Serializable
    data object Camera : CameraDestination

    @Serializable
    data object CapturePreview : CameraDestination
}

@Composable
fun CameraNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = CameraDestination.Camera
    ) {
        var capturedImageObject = CapturedImage()

        composable<CameraDestination.Camera> {
            CameraScreen(
                onClick = {
                    capturedImageObject = capturedImageObject.apply { capturedImage = it }
                    navController.navigate(CameraDestination.CapturePreview)
                }
            )
        }
        composable<CameraDestination.CapturePreview> {
            CapturePreviewScreen(
                image = capturedImageObject.capturedImage,
                onRetakeClick = {
                    capturedImageObject = capturedImageObject.apply { capturedImage = null }
                    navController.navigateUp()
                },
            )
        }
    }
}