package com.anshtya.jetx.common.model

import java.time.ZonedDateTime
import java.util.UUID

data class Message(
    val id: UUID,
    val senderId: UUID,
    val text: String,
    val isStarred: Boolean,
    val createdAt: ZonedDateTime,
    val status: MessageStatus
)