package com.anshtya.jetx.settings.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.settings.ui.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
private data object Settings

fun NavGraphBuilder.settings() {
    navigation<Settings>(
        startDestination = SettingsDestinations.SettingList
    ) {
        composable<SettingsDestinations.SettingList> {
            SettingsScreen()
        }

        // Other screens...
    }
}

fun NavController.navigateToSettings() {
    navigate(Settings)
}