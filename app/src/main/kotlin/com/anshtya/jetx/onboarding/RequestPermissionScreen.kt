package com.anshtya.jetx.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.anshtya.jetx.R
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.util.horizontalPadding
import com.anshtya.jetx.util.verticalPadding

private data class RequestedPermission(
    val permission: String,
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int
)

@Composable
fun RequestPermissionRoute(
    onNavigateUp: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    RequestPermissionScreen(
        onBackClick = onNavigateUp,
        onPermissionsGranted = viewModel::onOnboardingComplete
    )
}

@Composable
private fun RequestPermissionScreen(
    onBackClick: () -> Unit,
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    val permissions = remember {
        buildList {
            add(
                RequestedPermission(
                    permission = Manifest.permission.CAMERA,
                    icon = Icons.Default.CameraAlt,
                    titleRes = R.string.permission_camera_title,
                    descriptionRes = R.string.permission_camera_text
                )
            )
            add(
                RequestedPermission(
                    permission = Manifest.permission.RECORD_AUDIO,
                    icon = Icons.Default.Mic,
                    titleRes = R.string.permission_microphone_title,
                    descriptionRes = R.string.permission_microphone_text
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(
                    RequestedPermission(
                        permission = Manifest.permission.POST_NOTIFICATIONS,
                        icon = Icons.Default.Notifications,
                        titleRes = R.string.permission_notifications_title,
                        descriptionRes = R.string.permission_notifications_text
                    )
                )
            }
        }
    }

    var grantedPermissions by remember {
        mutableStateOf(
            permissions
                .filter {
                    ContextCompat.checkSelfPermission(context, it.permission) ==
                        PackageManager.PERMISSION_GRANTED
                }
                .map { it.permission }
                .toSet()
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        grantedPermissions = grantedPermissions + result.filterValues { it }.keys
        onPermissionsGranted()
    }

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                navigationIcon = { BackButton(onBackClick) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_permission_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.onboarding_permission_text),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                permissions.forEach { item ->
                    PermissionRow(
                        item = item,
                        granted = grantedPermissions.contains(item.permission)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val notGranted = permissions
                        .map { it.permission }
                        .filterNot { grantedPermissions.contains(it) }
                    if (notGranted.isEmpty()) {
                        onPermissionsGranted()
                    } else {
                        launcher.launch(notGranted.toTypedArray())
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = stringResource(id = R.string.continue_text))
            }
        }
    }
}

@Composable
private fun PermissionRow(
    item: RequestedPermission,
    granted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = item.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = item.descriptionRes),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (granted) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RequestPermissionScreenPreview() {
    RequestPermissionScreen(
        onBackClick = {},
        onPermissionsGranted = {}
    )
}
