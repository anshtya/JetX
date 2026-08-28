package com.anshtya.jetx.chats.data

import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.core.database.model.MessageWithAttachment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MessagesRepository {
    fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>>

    suspend fun insertMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentFormat: AttachmentFormat,
        currentUser: Boolean
    ): Int

    suspend fun markMessageReceivedRemote(id: UUID): Result<Unit>

    suspend fun markChatMessagesAsSeen(chatId: Int)

    suspend fun deleteMessages(ids: List<Int>)
}
