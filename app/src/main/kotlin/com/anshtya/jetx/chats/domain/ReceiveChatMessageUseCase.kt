package com.anshtya.jetx.chats.domain

import android.util.Log
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.notifications.DefaultNotificationManager
import com.anshtya.jetx.profile.data.ProfileRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Saves an incoming chat message locally, acknowledges receipt to the server
 * and posts a notification for it.
 *
 * Coordinates the [MessagesRepository], [AttachmentRepository] and [ProfileRepository]
 * so a single message receive is atomic from the caller's point of view.
 */
class ReceiveChatMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val profileRepository: ProfileRepository,
    private val attachmentRepository: AttachmentRepository,
    private val defaultNotificationManager: DefaultNotificationManager
) {
    private val tag = this::class.simpleName

    suspend operator fun invoke(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentId: UUID?
    ): Result<Unit> = try {
        val networkAttachment = attachmentId?.let {
            attachmentRepository.getAttachment(it)
                .getOrElse { throwable ->
                    Log.e(tag, "Failed to get attachment", throwable)
                    return Result.failure(throwable)
                }
        }

        profileRepository.fetchAndSaveProfile(senderId)

        val messageId = messagesRepository.insertMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = if (networkAttachment != null) {
                AttachmentFormat.ServerAttachment(networkAttachment)
            } else AttachmentFormat.None,
            currentUser = false
        )
        messagesRepository.markMessageReceivedRemote(id).getOrThrow()

        defaultNotificationManager.postChatNotification(messageId)

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Error receiving chat message", e)
        Result.failure(e)
    }
}
