package com.anshtya.jetx.settings.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.settings.ui.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
private data object SettingsGraph

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    navigation<SettingsGraph>(
        startDestination = SettingsDestination.SettingList
    ) {
        composable<SettingsDestination.SettingList> {
            SettingsRoute(
                onBackClick = navController::navigateUp
            )
        }

        // Other screens...
    }
}

fun NavController.navigateToSettingsGraph() {
    navigate(SettingsGraph)
}