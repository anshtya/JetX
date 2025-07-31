package com.anshtya.jetx.chats.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ChatsDestination {
    @Serializable
    data object ChatList : ChatsDestination

    @Serializable
    data class Chat(
        val recipientId: String? = null,
        val chatId: Int? = null,
        val username: String? = null,
        val pictureUrl: String? = null
    ) : ChatsDestination

    @Serializable
    data object ArchivedChatList : ChatsDestination

    @Serializable
    data class Media(val data: String)
}