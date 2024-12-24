package com.anshtya.jetx.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {

    SettingsScreen(onSignOutClick = viewModel::onSignOutClick)
}

@Composable
private fun SettingsScreen(
    onSignOutClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            Button(onSignOutClick) { Text("Sign out") }
        }
    }
}