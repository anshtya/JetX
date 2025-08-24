package com.anshtya.jetx.ui.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

fun NavGraphBuilder.mainDestination(
    onNavigateToSettings: () -> Unit,
) {
    composable<MainRoute> {
        MainScreen(onNavigateToSettings = onNavigateToSettings)
    }
}