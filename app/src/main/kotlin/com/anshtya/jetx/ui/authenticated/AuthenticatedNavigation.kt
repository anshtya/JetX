package com.anshtya.jetx.ui.authenticated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.anshtya.jetx.calls.ui.navigation.calls
import com.anshtya.jetx.chats.ui.navigation.ChatsGraphRoute
import com.anshtya.jetx.chats.ui.navigation.chatsGraph
import com.anshtya.jetx.settings.ui.navigation.navigateToSettingsGraph
import com.anshtya.jetx.settings.ui.navigation.settingsGraph
import kotlinx.serialization.Serializable

@Serializable
data object AuthenticatedGraph

fun NavGraphBuilder.authenticatedGraph(
    navController: NavController
) {
    navigation<AuthenticatedGraph>(
        startDestination = ChatsGraphRoute
    ) {
        chatsGraph(
            navController = navController,
            onNavigateToSettings = navController::navigateToSettingsGraph
        )
        calls()
        settingsGraph(
            navController = navController
        )
    }
}