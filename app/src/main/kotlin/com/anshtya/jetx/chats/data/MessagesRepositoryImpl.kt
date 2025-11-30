package com.anshtya.jetx.chats.data

import android.net.Uri
import android.util.Log
import androidx.work.WorkManager
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.database.dao.ChatDao
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.core.database.model.MessageWithAttachment
import com.anshtya.jetx.core.network.service.AttachmentService
import com.anshtya.jetx.core.network.service.MessageService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.notifications.DefaultNotificationManager
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.work.worker.MessageSendWorker
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val profileRepository: ProfileRepository,
    private val attachmentRepository: AttachmentRepository,
    private val messageService: MessageService,
    private val attachmentService: AttachmentService,
    private val authManager: AuthManager,
    private val workManager: WorkManager,
    private val defaultNotificationManager: DefaultNotificationManager
) : MessagesRepository {
    private val tag = this::class.simpleName

    override fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        localMessagesDataSource.getChatMessages(chatId)

    override suspend fun receiveChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentId: UUID?
    ): Result<Unit> = try {
        val networkAttachment = attachmentId?.let {
            attachmentService.getAttachment(it)
                .toResult()
                .getOrElse { throwable ->
                    Log.e(tag, "Failed to get attachment", throwable)
                    return Result.failure(throwable)
                }
        }

        val messageId = saveChatMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = if (networkAttachment != null) {
                AttachmentFormat.ServerAttachment(networkAttachment)
            } else AttachmentFormat.None,
            currentUser = false
        ).getOrThrow()
        messageService.markMessageReceived(id).toResult().getOrThrow()

        defaultNotificationManager.postChatNotification(messageId)

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Error receiving chat message")
        Result.failure(e)
    }

    override suspend fun sendChatMessage(
        chatId: Int,
        text: String?,
        attachmentUri: Uri?
    ): Result<Unit> = runCatching {
        val recipientId = chatDao.getChatRecipientId(chatId)
        sendChatMessage(recipientId, text, attachmentUri)
        Result.success(Unit)
    }

    override suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?,
        attachmentUri: Uri?
    ): Result<Unit> = try {
        val attachmentStorageUri = attachmentUri?.let {
            attachmentRepository.migrateToStorage(it).getOrNull()
        }
        val messageId = saveChatMessage(
            id = UUID.randomUUID(),
            senderId = authManager.authState.value.currentUserIdOrNull()!!,
            recipientId = recipientId,
            text = text,
            attachmentFormat = if (attachmentStorageUri != null) {
                AttachmentFormat.UriAttachment(
                    uri = attachmentStorageUri,
                    attachmentMetadata = attachmentRepository.getAttachmentMetadata(
                        uri = attachmentStorageUri
                    ).getOrThrow()
                )
            } else AttachmentFormat.None,
            currentUser = true
        ).getOrThrow()
        MessageSendWorker.scheduleWork(workManager, messageId)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Error sending chat message", e)
        Result.failure(e)
    }

    override suspend fun markChatMessagesAsSeen(chatId: Int) {
        val unreadMessageIds = localMessagesDataSource.markChatMessagesAsSeen(chatId)
        Log.i("message", "unread ids - $unreadMessageIds")
        messageService.markMessagesSeen(unreadMessageIds)
            .toResult()
            .getOrElse {
                Log.w(tag, "Failed to mark messages as seen", it)
            }
    }

    private suspend fun saveChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentFormat: AttachmentFormat,
        currentUser: Boolean
    ): Result<Int> = try {
        val profileId = if (currentUser) recipientId else senderId
        profileRepository.fetchAndSaveProfile(profileId)

        val messageId = localMessagesDataSource.insertMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = attachmentFormat,
            currentUser = currentUser
        )

        Result.success(messageId)
    } catch (e: Exception) {
        Log.e(tag, "Error saving chat message", e)
        Result.failure(e)
    }

    override suspend fun deleteMessages(ids: List<Int>) {
        localMessagesDataSource.deleteMessages(ids)
    }
}