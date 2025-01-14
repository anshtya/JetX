package com.anshtya.jetx.chats.ui.chat

import java.util.UUID

data class RecipientUser(
    val id: UUID,
    val username: String,
    val pictureUrl: String?
)
