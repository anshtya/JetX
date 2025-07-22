package com.anshtya.jetx.chats.data

import android.net.Uri
import android.util.Log
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.attachments.data.NetworkAttachment
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.model.MessageWithAttachment
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.util.Constants
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import com.anshtya.jetx.work.WorkScheduler
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
    private val attachmentRepository: AttachmentRepository,
    private val workScheduler: WorkScheduler
) : MessagesRepository {
    private val tag = this::class.simpleName
    private val supabaseAuth = client.auth
    private val networkMessagesTable = client.from(MESSAGE_TABLE)
    private val attachmentTable = client.from(Constants.ATTACHMENT_TABLE)

    override fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        localMessagesDataSource.getChatMessages(chatId)

    override suspend fun receiveChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentId: String
    ): Result<Int> = try {
        val networkAttachment = if (attachmentId.isNotBlank()) {
            attachmentTable.select {
                filter { eq("id", attachmentId.toInt()) }
            }.decodeSingle<NetworkAttachment>()
        } else null

        val message = saveChatMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentFormat = if (networkAttachment != null) {
                AttachmentFormat.UrlAttachment(networkAttachment)
            } else AttachmentFormat.None,
            currentUser = false
        ).getOrThrow()
        networkMessagesTable.update(
            update = { set("has_received", true) },
            request = {
                filter { eq("id", message.uid) }
            }
        )
        Result.success(message.chatId)
    } catch (e: Exception) {
        Log.e(tag, "Error receiving chat message - ${e.message}")
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
        val message = saveChatMessage(
            id = UUID.randomUUID(),
            senderId = UUID.fromString(supabaseAuth.currentUserOrNull()?.id),
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
        workScheduler.createMessageSendWork(message.uid)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Error sending chat message - ${e.message}")
        Result.failure(e)
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
    ): Result<MessageEntity> = try {
        val profileId = if (currentUser) recipientId else senderId
        val profileExists = profileRepository.getProfile(profileId)
        if (profileExists == null) {
            profileRepository.saveProfile(profileId.toString())
        }
        Result.success(
            localMessagesDataSource.insertMessage(
                id = id,
                senderId = senderId,
                recipientId = recipientId,
                text = text,
                attachmentFormat = attachmentFormat,
                currentUser = currentUser
            )
        )
    } catch (e: Exception) {
        Log.e(tag, "Error saving chat message - ${e.message}")
        Result.failure(e)
    }

    override suspend fun deleteMessages(ids: List<Int>) {
        localMessagesDataSource.deleteMessages(ids)
    }
}