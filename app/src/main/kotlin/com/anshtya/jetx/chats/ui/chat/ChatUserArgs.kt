package com.anshtya.jetx.chats.ui.chat

import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import com.anshtya.jetx.core.model.Chat
import com.anshtya.jetx.core.model.UserProfile
import java.util.UUID

data class ChatUserArgs(
    val recipientId: UUID? = null,
    val chatId: Int? = null,
    val username: String? = null,
    val pictureUrl: String? = null
)

fun UserProfile.toChatUserArgs(): ChatUserArgs {
    return ChatUserArgs(
        recipientId = id,
        username = username,
        pictureUrl = pictureUrl
    )
}

fun Chat.toChatUserArgs(): ChatUserArgs {
    return ChatUserArgs(chatId = id)
}

fun ChatUserArgs.toChatDestination(): ChatsDestination.Chat {
    return ChatsDestination.Chat(
        recipientId = recipientId?.toString(),
        chatId = chatId,
        username = username,
        pictureUrl = pictureUrl
    )
}