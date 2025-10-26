package com.anshtya.jetx.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar

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
    // TODO: implement permission screen
    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                navigationIcon = { BackButton(onBackClick) }
            )
        }
    ) {
        Column {
            Text("request perms")
            Button(onPermissionsGranted) { Text("next" )}
        }
    }
}