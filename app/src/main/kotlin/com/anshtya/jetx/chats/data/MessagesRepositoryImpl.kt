package com.anshtya.jetx.chats.data

import android.util.Log
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.core.database.model.MessageWithAttachment
import com.anshtya.jetx.core.network.service.MessageService
import com.anshtya.jetx.core.network.util.toResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val messageService: MessageService
) : MessagesRepository {
    private val tag = this::class.simpleName

    override fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        localMessagesDataSource.getChatMessages(chatId)

    override suspend fun insertMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentFormat: AttachmentFormat,
        currentUser: Boolean
    ): Int = localMessagesDataSource.insertMessage(
        id = id,
        senderId = senderId,
        recipientId = recipientId,
        text = text,
        attachmentFormat = attachmentFormat,
        currentUser = currentUser
    )

    override suspend fun markMessageReceivedRemote(id: UUID): Result<Unit> =
        messageService.markMessageReceived(id).toResult()

    override suspend fun markChatMessagesAsSeen(chatId: Int) {
        val unreadMessageIds = localMessagesDataSource.markChatMessagesAsSeen(chatId)
        messageService.markMessagesSeen(unreadMessageIds)
            .toResult()
            .getOrElse {
                Log.w(tag, "Failed to mark messages as seen", it)
            }
    }

    override suspend fun deleteMessages(ids: List<Int>) {
        localMessagesDataSource.deleteMessages(ids)
    }
}
