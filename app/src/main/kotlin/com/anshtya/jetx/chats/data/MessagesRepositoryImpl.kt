package com.anshtya.jetx.chats.data

import android.net.Uri
import android.util.Log
import com.anshtya.jetx.attachments.AttachmentFormat
import com.anshtya.jetx.attachments.AttachmentManager
import com.anshtya.jetx.attachments.AttachmentType
import com.anshtya.jetx.chats.data.model.toNetworkMessage
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.model.MessageWithAttachment
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val profileRepository: ProfileRepository,
    private val attachmentManager: AttachmentManager
) : MessagesRepository, MessageReceiveRepository {
    private val supabaseAuth = client.auth
    private val networkMessagesTable = client.from(MESSAGE_TABLE)

    override fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        localMessagesDataSource.getChatMessages(chatId)

    override suspend fun insertChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachment: AttachmentFormat
    ): Int {
        val message = saveChatMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = attachment,
            currentUser = false
        )
        networkMessagesTable.update(
            update = { set("has_received", true) },
            request = {
                filter { eq("id", message.uid) }
            }
        )
        return message.chatId
    }

    override suspend fun sendChatMessage(chatId: Int, text: String) {
        val recipientId = chatDao.getChatRecipientId(chatId)
        sendChatMessage(recipientId, text, attachmentUri = null)
    }

    override suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?,
        attachmentUri: Uri?
    ) {
        val message = saveChatMessage(
            id = UUID.randomUUID(),
            senderId = UUID.fromString(supabaseAuth.currentUserOrNull()?.id),
            recipientId = recipientId,
            text = text,
            attachmentFormat = if (attachmentUri != null) {
                AttachmentFormat.UriAttachment(
                    uri = attachmentUri,
                    type = AttachmentType.fromMimeType(
                        attachmentManager.getMimeTypeFromUri(attachmentUri)
                    )!!
                )
            } else AttachmentFormat.None,
            currentUser = true
        )

        val attachmentId = if (attachmentUri != null) {
            attachmentManager.uploadMediaAttachment(attachmentUri)
        } else null
        networkMessagesTable.insert(message.toNetworkMessage(attachmentId))
        localMessagesDataSource.updateMessageStatus(message.uid, MessageStatus.SENT)
    }

    override suspend fun markChatMessagesAsSeen(chatId: Int) {
        val unreadMessageIds = localMessagesDataSource.markChatMessagesAsSeen(chatId)
        Log.i("message", "unread ids - $unreadMessageIds")
        unreadMessageIds.forEach { messageId ->
            networkMessagesTable.update(
                update = { set("has_seen", true) },
                request = {
                    filter { eq("id", messageId) }
                }
            )
        }
    }

    private suspend fun saveChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentFormat: AttachmentFormat,
        currentUser: Boolean
    ): MessageEntity {
        val profileId = if (currentUser) recipientId else senderId
        val profileExists = profileRepository.getProfile(profileId)
        if (profileExists == null) {
            profileRepository.saveProfile(profileId.toString())
        }
        return localMessagesDataSource.insertMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = attachmentFormat,
            currentUser = currentUser
        )
    }

    override suspend fun deleteMessages(ids: List<Int>) {
        localMessagesDataSource.deleteMessages(ids)
    }
}