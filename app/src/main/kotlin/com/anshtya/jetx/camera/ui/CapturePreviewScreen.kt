package com.anshtya.jetx.camera.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anshtya.jetx.R
import com.anshtya.jetx.util.Constants

@Composable
fun CapturePreviewRoute(
    image: Bitmap?,
    onRetakeClick: () -> Unit,
    viewModel: CapturePreviewViewModel = hiltViewModel()
) {
    CapturePreviewScreen(
        image = image,
        loading = viewModel.loading,
        savedImageUri = viewModel.savedImageUri,
        errorMessage = viewModel.errorMessage,
        saveImage = viewModel::saveImage,
        onErrorShown = viewModel::errorShown,
        onRetakeClick = onRetakeClick
    )
}


@Composable
private fun CapturePreviewScreen(
    image: Bitmap?,
    loading: Boolean,
    savedImageUri: Uri?,
    errorMessage: String?,
    saveImage: (Bitmap) -> Unit,
    onErrorShown: () -> Unit,
    onRetakeClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(savedImageUri) {
        savedImageUri?.let {
            val activity = context as Activity
            activity.setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constants.PHOTO_INTENT_KEY, it) })
            activity.finish()
        }
    }

    Column(Modifier.fillMaxSize()) {
        errorMessage?.let {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            onErrorShown()
        }

        Box(Modifier.weight(1f)) {
            image?.let {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Button(
                onClick = onRetakeClick
            ) {
                Text(text = stringResource(id = R.string.capture_preview_retake))
            }

            Button(
                onClick = { saveImage(image!!) }
            ) {
                Text(text = stringResource(id = R.string.capture_preview_send))
            }
        }
    }
}