package com.anshtya.jetx.chats.domain

import android.net.Uri
import android.util.Log
import androidx.work.WorkManager
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.work.worker.MessageSendWorker
import java.util.UUID
import javax.inject.Inject

/**
 * Saves an outgoing chat message locally and schedules it for delivery.
 *
 * Coordinates the [MessagesRepository], [AttachmentRepository] and [ProfileRepository]
 * so a single message send is atomic from the caller's point of view.
 */
class SendChatMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val chatsRepository: ChatsRepository,
    private val profileRepository: ProfileRepository,
    private val attachmentRepository: AttachmentRepository,
    private val authManager: AuthManager,
    private val workManager: WorkManager
) {
    private val tag = this::class.simpleName

    suspend operator fun invoke(
        chatId: Int,
        text: String?,
        attachmentUri: Uri?
    ): Result<Unit> = runCatching {
        val recipientId = chatsRepository.getChatRecipientId(chatId)
        invoke(recipientId, text, attachmentUri).getOrThrow()
    }

    suspend operator fun invoke(
        recipientId: UUID,
        text: String?,
        attachmentUri: Uri?
    ): Result<Unit> = try {
        val attachmentStorageUri = attachmentUri?.let {
            attachmentRepository.migrateToStorage(it).getOrNull()
        }

        profileRepository.fetchAndSaveProfile(recipientId)

        val messageId = messagesRepository.insertMessage(
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
        )
        MessageSendWorker.scheduleWork(workManager, messageId)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Error sending chat message", e)
        Result.failure(e)
    }
}
