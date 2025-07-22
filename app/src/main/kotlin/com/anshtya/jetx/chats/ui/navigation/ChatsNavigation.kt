package com.anshtya.jetx.chats.ui.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.anshtya.jetx.attachments.ui.MediaScreen
import com.anshtya.jetx.chats.ui.archivedchatlist.ArchivedChatListRoute
import com.anshtya.jetx.chats.ui.chat.ChatRoute
import com.anshtya.jetx.chats.ui.chat.toChatDestination
import com.anshtya.jetx.chats.ui.chatlist.ChatListRoute
import com.anshtya.jetx.chats.ui.chatlist.ChatListViewModel
import com.anshtya.jetx.util.Constants
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
        composable<ChatsDestinations.ChatList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Chats)
            }
            ChatListRoute(
                onNavigateToChat = { args ->
                    navController.navigate(args.toChatDestination())
                },
                onNavigateToArchivedChats = {
                    navController.navigate(ChatsDestinations.ArchivedChatList)
                },
                onNavigateToSettings = onNavigateToSettings,
                viewModel = hiltViewModel<ChatListViewModel>(parentEntry)
            )
        }

        composable<ChatsDestinations.ArchivedChatList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Chats)
            }
            ArchivedChatListRoute(
                onNavigateToChat = { args ->
                    navController.navigate(args.toChatDestination())
                },
                onBackClick = navController::navigateUp,
                viewModel = hiltViewModel<ChatListViewModel>(parentEntry)
            )
        }

        composable<ChatsDestinations.Chat>(
            deepLinks = listOf(
                navDeepLink<ChatsDestinations.Chat>(
                    basePath = "${Constants.BASE_APP_URL}/${Constants.CHAT_ARG}"
                )
            )
        ) {
            ChatRoute(
                onNavigateToMediaScreen = { data ->
                    navController.navigate(ChatsDestinations.Media(data))
                },
                onBackClick = navController::navigateUp
            )
        }

        composable<ChatsDestinations.Media> { backStackEntry ->
            MediaScreen(
                data = backStackEntry.toRoute<ChatsDestinations.Media>().data,
                onBackClick = navController::navigateUp
            )
        }
    }
}