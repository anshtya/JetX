package com.anshtya.jetx.ui.navigation.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.anshtya.jetx.calls.ui.navigation.calls
import com.anshtya.jetx.chats.ui.navigation.Chats
import com.anshtya.jetx.chats.ui.navigation.chats
import com.anshtya.jetx.settings.ui.navigation.navigateToSettings
import com.anshtya.jetx.settings.ui.navigation.settings
import kotlinx.serialization.Serializable

@Serializable
data object HomeGraph

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onNavigateToAuth: () -> Unit
) {
    navigation<HomeGraph>(
        startDestination = Chats
    ) {
        chats(
            navController = navController,
            onNavigateToSettings = navController::navigateToSettings
        )
        calls()
        settings(
            navController = navController,
            onNavigateToAuth = onNavigateToAuth
        )
    }
}