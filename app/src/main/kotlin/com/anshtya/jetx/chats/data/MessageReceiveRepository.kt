package com.anshtya.jetx.chats.data

import com.anshtya.jetx.attachments.AttachmentFormat
import java.util.UUID

interface MessageReceiveRepository {
    suspend fun insertChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachment: AttachmentFormat
    ): Int
}