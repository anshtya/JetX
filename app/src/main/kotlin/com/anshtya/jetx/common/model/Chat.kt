package com.anshtya.jetx.common.model

import java.util.UUID

data class Chat(
    val id: Int,
    val recipientId: UUID,
    val username: String,
    val profilePicture: String?,
    val message: String,
    val unreadCount: Int,
    val timestamp: String,
    val messageStatus: MessageStatus,
    val isSender: Boolean
)
