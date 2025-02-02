package com.anshtya.jetx.chats.data

import java.util.UUID

interface MessageReceiveRepository {
    suspend fun insertChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentUri: String?
    )
}