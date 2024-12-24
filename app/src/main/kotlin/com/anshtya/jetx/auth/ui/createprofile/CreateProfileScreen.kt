package com.anshtya.jetx.auth.ui.createprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateProfileRoute(
    onNavigateToHome: () -> Unit,
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateProfileScreen(
        uiState = uiState,
        onProfileCreated = {
            viewModel.onContinueClick()
            onNavigateToHome()
        },
    )
}

@Composable
private fun CreateProfileScreen(
    uiState: CreateProfileUiState,
    onProfileCreated: () -> Unit
) {
    LaunchedEffect(uiState.profileCreated) {
        if (uiState.profileCreated) onProfileCreated()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            Button(onProfileCreated) { Text("Continue") }
        }
    }
}