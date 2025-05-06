package com.anshtya.jetx.chats.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.anshtya.jetx.R

@Composable
fun NotificationPermissionHandler() {
    val context = LocalContext.current

    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var showRationaleDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            if ((context as Activity).shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == true
            ) {
                showRationaleDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        if (needsPermission && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (showRationaleDialog && needsPermission) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(text = stringResource(id = R.string.notification_permission_title)) },
            text = { Text(text = stringResource(id = R.string.notification_permission_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) {
                    Text(text = stringResource(id = R.string.notification_permission_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                }) {
                    Text(text = stringResource(id = R.string.notification_permission_dismiss))
                }
            }
        )
    }
}