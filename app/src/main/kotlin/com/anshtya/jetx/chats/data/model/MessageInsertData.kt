package com.anshtya.jetx.chats.data.model

import java.util.UUID

data class MessageInsertData(
    val chatId: Int,
    val recipientId: UUID,
    val message: String? = null,
    val attachment: String? = null
)
