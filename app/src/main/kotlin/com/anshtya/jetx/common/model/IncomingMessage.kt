package com.anshtya.jetx.common.model

import java.util.UUID

data class IncomingMessage(
    val id: UUID,
    val senderId: UUID,
    val recipientId: UUID,
    val text: String?,
    val attachmentUri: String?,
    val status: MessageStatus
)