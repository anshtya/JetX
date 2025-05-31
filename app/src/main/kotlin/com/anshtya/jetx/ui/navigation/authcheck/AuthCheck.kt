package com.anshtya.jetx.ui.navigation.authcheck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable

@Serializable
data object AuthCheck

@Composable
fun AuthCheck(
    onNavigateToChatList: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToCreateProfile: () -> Unit,
    viewModel: AuthCheckViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()

    LaunchedEffect(userState) {
        if (userState is AuthCheckUiState.Success) {
            val state = userState as AuthCheckUiState.Success
            if (state.authenticated && state.profileCreated) {
                onNavigateToChatList()
            } else if (!state.authenticated) {
                onNavigateToAuth()
            } else {
                onNavigateToCreateProfile()
            }
        }
    }
}