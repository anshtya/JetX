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
data object ChatsGraphRoute

fun NavGraphBuilder.chatsGraph(
    navController: NavController,
    onNavigateToSettings: () -> Unit
) {
    navigation<ChatsGraphRoute>(
        startDestination = ChatsDestination.ChatList
    ) {
        composable<ChatsDestination.ChatList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(ChatsGraphRoute)
            }
            ChatListRoute(
                onNavigateToChat = { args ->
                    navController.navigate(args.toChatDestination())
                },
                onNavigateToArchivedChats = {
                    navController.navigate(ChatsDestination.ArchivedChatList)
                },
                onNavigateToSettings = onNavigateToSettings,
                viewModel = hiltViewModel<ChatListViewModel>(parentEntry)
            )
        }

        composable<ChatsDestination.ArchivedChatList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(ChatsGraphRoute)
            }
            ArchivedChatListRoute(
                onNavigateToChat = { args ->
                    navController.navigate(args.toChatDestination())
                },
                onBackClick = navController::navigateUp,
                viewModel = hiltViewModel<ChatListViewModel>(parentEntry)
            )
        }

        composable<ChatsDestination.Chat>(
            deepLinks = listOf(
                navDeepLink<ChatsDestination.Chat>(
                    basePath = "${Constants.BASE_APP_URL}/${Constants.CHAT_ARG}"
                )
            )
        ) {
            ChatRoute(
                onNavigateToMediaScreen = { data ->
                    navController.navigate(ChatsDestination.Media(data))
                },
                onBackClick = navController::navigateUp
            )
        }

        composable<ChatsDestination.Media> { backStackEntry ->
            MediaScreen(
                data = backStackEntry.toRoute<ChatsDestination.Media>().data,
                onBackClick = navController::navigateUp
            )
        }
    }
}