package com.anshtya.jetx.settings.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.settings.ui.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
private data object Settings

fun NavGraphBuilder.settings(
    navController: NavController,
    onNavigateToAuth: () -> Unit
) {
    navigation<Settings>(
        startDestination = SettingsDestinations.SettingList
    ) {
        composable<SettingsDestinations.SettingList> {
            SettingsRoute(
                onNavigateToAuth = onNavigateToAuth,
                onBackClick = navController::navigateUp
            )
        }

        // Other screens...
    }
}

fun NavController.navigateToSettings() {
    navigate(Settings)
}