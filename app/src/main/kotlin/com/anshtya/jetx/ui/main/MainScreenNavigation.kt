package com.anshtya.jetx.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.anshtya.jetx.settings.navigation.navigateToSettingsGraph
import com.anshtya.jetx.settings.navigation.settingsGraph
import kotlinx.serialization.Serializable

@Serializable
data object MainGraph

fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    navigation<MainGraph>(
        startDestination = MainScreenWithNavBar
    ) {
        mainScreenWithNavBar(
            onNavigateToSettings = navController::navigateToSettingsGraph
        )
        settingsGraph(navController)
    }
}