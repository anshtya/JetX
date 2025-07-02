package com.anshtya.jetx.camera.permission

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.ComponentPreview

@Composable
fun CameraPermissionRequest(
    permissionsDenied: Boolean,
    deniedPermissions: List<String>,
    onPermissionRequest: () -> Unit,
    onGoToAppInfo: () -> Unit,
    onNotNowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (permissionsDenied) {
                stringResource(id = R.string.camera_permissions_not_allowed)
            } else {
                stringResource(id = R.string.camera_permission_title)
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (permissionsDenied) {
                if (deniedPermissions.size > 1) {
                    stringResource(
                        id = R.string.multiple_permission_enable,
                        deniedPermissions.joinToString { getPresentablePermissionText(it) }
                    )
                } else {
                    stringResource(
                        id = R.string.permission_enable,
                        getPresentablePermissionText(deniedPermissions[0])
                    )
                }
            } else {
                stringResource(id = R.string.camera_permission_text)
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = if (permissionsDenied) onGoToAppInfo else onPermissionRequest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (permissionsDenied) {
                    stringResource(id = R.string.go_to_app_info)
                } else {
                    stringResource(id = R.string.continue_text)
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = onNotNowClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.camera_permission_not_now))
        }
    }
}

private fun getPresentablePermissionText(text: String): String = when (text) {
    Manifest.permission.CAMERA -> "Camera"
    Manifest.permission.RECORD_AUDIO -> "Microphone"
    else -> ""
}

@Preview
@Composable
private fun CameraPermissionDeniedPreview() {
    ComponentPreview {
        CameraPermissionRequest(
            permissionsDenied = false,
            deniedPermissions = listOf(Manifest.permission.CAMERA),
            onPermissionRequest = {},
            onGoToAppInfo = {},
            onNotNowClick = {},
        )
    }
}