package com.anshtya.jetx.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.navigation.archivedChatListScreen
import com.anshtya.jetx.chats.ui.navigation.chatGraph
import com.anshtya.jetx.chats.ui.navigation.navigateToArchivedChatList
import com.anshtya.jetx.chats.ui.navigation.navigateToChat
import com.anshtya.jetx.settings.navigation.navigateToSettingsGraph
import com.anshtya.jetx.settings.navigation.settingsGraph
import kotlinx.serialization.Serializable

@Serializable
data object MainGraph

@Serializable
data object MainScreenWithNavBar

fun NavGraphBuilder.mainScreenWithNavBar(
    onNavigateToSettings: () -> Unit,
    onNavigateToArchivedChatList: () -> Unit,
    onNavigateToChat: (ChatUserArgs) -> Unit,
) {
    composable<MainScreenWithNavBar> {
        MainScreen(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToArchivedChatList = onNavigateToArchivedChatList,
            onNavigateToChat = onNavigateToChat
        )
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    navigation<MainGraph>(
        startDestination = MainScreenWithNavBar
    ) {
        mainScreenWithNavBar(
            onNavigateToSettings = navController::navigateToSettingsGraph,
            onNavigateToChat = navController::navigateToChat,
            onNavigateToArchivedChatList = navController::navigateToArchivedChatList
        )
        archivedChatListScreen(
            onNavigateUp = navController::navigateUp,
            onNavigateToChat = navController::navigateToChat
        )
        chatGraph(navController)
        settingsGraph(navController)
    }
}