package com.anshtya.jetx.common.model

import java.util.UUID

data class Message(
    val id: Int,
    val senderId: UUID,
    val text: String,
    val isStarred: Boolean,
    val createdAt: String,
    val status: MessageStatus
)