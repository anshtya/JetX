package com.anshtya.jetx.chats.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.chats.ui.ChatsRoute
import kotlinx.serialization.Serializable

@Serializable
data object Chats

fun NavGraphBuilder.chats(
    onNavigateToSettings: () -> Unit
) {
    navigation<Chats>(
        startDestination = ChatsDestinations.ChatList
    ) {
        composable<ChatsDestinations.ChatList> {
            ChatsRoute(
                onNavigateToSettings = onNavigateToSettings
            )
        }

        composable<ChatsDestinations.Chat> {

        }
    }
}