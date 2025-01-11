package com.anshtya.jetx.chats.ui.chat

import android.os.Parcelable
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.common.util.UUIDSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Parcelize
data class ChatUserArgs(
    @Serializable(with = UUIDSerializer::class)
    val recipientId: UUID,
    val chatId: Int? = null,
    val username: String,
    val pictureUrl: String?
): Parcelable

fun UserProfile.toChatUserArgs(): ChatUserArgs {
    return ChatUserArgs(
        recipientId = id,
        username = username,
        pictureUrl = pictureUrl
    )
}

fun Chat.toChatUserArgs(): ChatUserArgs {
    return ChatUserArgs(
        chatId = id,
        recipientId = recipientId,
        username = username,
        pictureUrl = profilePicture
    )
}