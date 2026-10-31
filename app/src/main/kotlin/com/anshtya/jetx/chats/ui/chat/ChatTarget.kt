package com.anshtya.jetx.chats.ui.chat

import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import java.util.UUID

/**
 * The conversation the chat screen is opened for, resolved from the nullable
 * navigation arguments in [ChatsDestination.Chat].
 */
sealed interface ChatTarget {
    /** A conversation that already exists locally. */
    data class ExistingChat(val chatId: Int) : ChatTarget

    /** A conversation with a user that has not been messaged yet, so it has no chat id. */
    data class NewChat(
        val recipientId: UUID,
        val username: String,
        val pictureUrl: String?
    ) : ChatTarget
}

/**
 * Returns the [ChatTarget] described by these arguments, or `null` if they don't
 * identify a conversation (e.g. a malformed deep link).
 */
fun ChatsDestination.Chat.toChatTarget(): ChatTarget? {
    if (chatId != null) return ChatTarget.ExistingChat(chatId)

    val parsedRecipientId = recipientId
        ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        ?: return null
    val recipientUsername = username ?: return null

    return ChatTarget.NewChat(
        recipientId = parsedRecipientId,
        username = recipientUsername,
        pictureUrl = pictureUrl
    )
}
