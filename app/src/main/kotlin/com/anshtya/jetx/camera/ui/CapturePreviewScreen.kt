package com.anshtya.jetx.camera.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R

@Composable
fun CapturePreviewScreen(
    image: Bitmap?,
    onRetakeClick: () -> Unit
) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            image?.let {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
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
                onClick = { sendImage(context, image) }
            ) {
                Text(text = stringResource(id = R.string.capture_preview_send))
            }
        }
    }
}

private fun sendImage(
    context: Context,
    image: Bitmap?
) {
    if (image == null) return

    val activity = context as Activity
    activity.setResult(Activity.RESULT_OK, Intent().apply { putExtra("photo", image) })
    activity.finish()
}