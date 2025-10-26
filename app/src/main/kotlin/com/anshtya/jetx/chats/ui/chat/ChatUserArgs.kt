package com.anshtya.jetx.chats.ui.chat

import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import com.anshtya.jetx.core.model.Chat
import java.util.UUID

data class ChatUserArgs(
    val recipientId: UUID? = null,
    val chatId: Int? = null,
    val username: String? = null,
    val pictureUrl: String? = null
)

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