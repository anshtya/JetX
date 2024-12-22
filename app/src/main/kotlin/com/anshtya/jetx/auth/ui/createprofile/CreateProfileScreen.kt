package com.anshtya.jetx.auth.ui.createprofile

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CreateProfileRoute(
    onNavigateToHome: () -> Unit
) {

    CreateProfileScreen(
        onProfileCreated = onNavigateToHome
    )
}

@Composable
private fun CreateProfileScreen(
    onProfileCreated: () -> Unit
) {
    Button(onProfileCreated) { Text("Continue") }
}