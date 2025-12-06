package com.anshtya.jetx.chats.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.anshtya.jetx.attachments.ui.MediaScreen
import com.anshtya.jetx.chats.ui.archivedchatlist.ArchivedChatListRoute
import com.anshtya.jetx.chats.ui.chat.ChatRoute
import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.chat.toChatDestination
import com.anshtya.jetx.chats.ui.chatlist.ChatListRoute
import com.anshtya.jetx.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object ChatGraphRoute

fun NavGraphBuilder.chatGraph(
    navController: NavController
) {
    navigation<ChatGraphRoute>(
        startDestination = ChatsDestination.Chat()
    ) {
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
                onNavigateUp = navController::navigateUp
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

fun NavGraphBuilder.chatListScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToArchivedChatList: () -> Unit,
    onNavigateToChat: (ChatUserArgs) -> Unit
) {
    composable<ChatsDestination.ChatList> {
        ChatListRoute(
            onNavigateToChat = onNavigateToChat,
            onNavigateToArchivedChats = onNavigateToArchivedChatList,
            onNavigateToSettings = onNavigateToSettings
        )
    }
}

fun NavGraphBuilder.archivedChatListScreen(
    onNavigateUp: () -> Unit,
    onNavigateToChat: (ChatUserArgs) -> Unit
) {
    composable<ChatsDestination.ArchivedChatList> {
        ArchivedChatListRoute(
            onNavigateToChat = onNavigateToChat,
            onBackClick = onNavigateUp
        )
    }
}

fun NavController.navigateToArchivedChatList() {
    navigate(ChatsDestination.ArchivedChatList)
}

fun NavController.navigateToChat(args: ChatUserArgs) {
    navigate(args.toChatDestination())
}