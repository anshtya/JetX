package com.anshtya.jetx.chats.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.chats.ui.chat.ChatRoute
import com.anshtya.jetx.chats.ui.chatlist.ChatListRoute
import kotlinx.serialization.Serializable

@Serializable
data object Chats

fun NavGraphBuilder.chats(
    navController: NavController,
    onNavigateToSettings: () -> Unit
) {
    navigation<Chats>(
        startDestination = ChatsDestinations.ChatList
    ) {
        composable<ChatsDestinations.ChatList> {
            ChatListRoute(
                onNavigateToChat = {
                    navController.navigate(ChatsDestinations.Chat(id = it))
                },
                onNavigateToSettings = onNavigateToSettings
            )
        }

        composable<ChatsDestinations.Chat> {
            ChatRoute()
        }
    }
}