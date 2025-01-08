package com.anshtya.jetx.chats.ui.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.anshtya.jetx.chats.ui.archivedchatlist.ArchivedChatListRoute
import com.anshtya.jetx.chats.ui.chat.ChatRoute
import com.anshtya.jetx.chats.ui.chatlist.ChatListRoute
import com.anshtya.jetx.chats.ui.chatlist.ChatListViewModel
import com.anshtya.jetx.chats.ui.search.SearchRoute
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
                onNavigateToChat = {
                    navController.navigate(ChatsDestinations.Chat(id = it))
                },
                onNavigateToArchivedChats = {
                    navController.navigate(ChatsDestinations.ArchivedChatList)
                },
                onNavigateToSearch = {
                    navController.navigate(ChatsDestinations.Search)
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
                onNavigateToChat = {
                    navController.navigate(ChatsDestinations.Chat(id = it))
                },
                onBackClick = navController::navigateUp,
                viewModel = hiltViewModel<ChatListViewModel>(parentEntry)
            )
        }

        composable<ChatsDestinations.Chat> {
            ChatRoute()
        }

        composable<ChatsDestinations.Search> {
            SearchRoute(
                onNavigateToChat = {
                    navController.navigate(ChatsDestinations.Chat(id = it))
                },
                onBackClick = navController::navigateUp
            )
        }
    }
}