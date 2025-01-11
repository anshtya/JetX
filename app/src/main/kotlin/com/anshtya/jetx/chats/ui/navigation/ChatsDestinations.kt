package com.anshtya.jetx.chats.ui.navigation

import com.anshtya.jetx.chats.ui.chat.ChatUserArgs
import com.anshtya.jetx.chats.ui.navigation.navtype.chatUserArgsType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

sealed interface ChatsDestinations {
    @Serializable
    data object ChatList : ChatsDestinations

    @Serializable
    data class Chat(val chatUserArgs: ChatUserArgs) : ChatsDestinations {
        companion object {
            val typeMap = mapOf(
                typeOf<ChatUserArgs>() to chatUserArgsType
            )
        }
    }

    @Serializable
    data object ArchivedChatList : ChatsDestinations

    @Serializable
    data object Search : ChatsDestinations
}