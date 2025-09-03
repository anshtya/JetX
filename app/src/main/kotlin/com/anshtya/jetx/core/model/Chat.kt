package com.anshtya.jetx.core.model

import com.anshtya.jetx.core.database.model.MessageStatus
import java.util.UUID

data class Chat(
    val id: Int,
    val recipientId: UUID,
    val username: String,
    val profilePicture: String?,
    val message: String?,
    val unreadCount: Int,
    val timestamp: String?,
    val messageStatus: MessageStatus?,
    val isSender: Boolean
)
