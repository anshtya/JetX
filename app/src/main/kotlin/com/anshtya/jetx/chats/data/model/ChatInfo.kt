package com.anshtya.jetx.chats.data.model

import com.anshtya.jetx.database.entity.ChatEntity
import java.util.UUID

data class ChatInfo(
    val id: Int,
    val recipientId: UUID,
)

fun ChatEntity.toChatInfo() = ChatInfo(
    id = id,
    recipientId = recipientId
)
