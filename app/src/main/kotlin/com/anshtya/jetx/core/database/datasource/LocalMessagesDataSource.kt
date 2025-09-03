package com.anshtya.jetx.core.database.datasource

import android.util.Log
import androidx.core.net.toFile
import androidx.room.withTransaction
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.database.dao.AttachmentDao
import com.anshtya.jetx.core.database.dao.ChatDao
import com.anshtya.jetx.core.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.core.database.dao.MessageDao
import com.anshtya.jetx.core.database.dao.RecentMessageDao
import com.anshtya.jetx.core.database.entity.AttachmentEntity
import com.anshtya.jetx.core.database.entity.ChatEntity
import com.anshtya.jetx.core.database.entity.MessageEntity
import com.anshtya.jetx.core.database.entity.RecentMessageEntity
import com.anshtya.jetx.core.database.model.MessageStatus
import com.anshtya.jetx.core.database.model.MessageWithAttachment
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class LocalMessagesDataSource @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val chatDao: ChatDao,
    private val db: JetXDatabase,
    private val messageDao: MessageDao,
    private val messageAttachmentsDao: MessageAttachmentsDao,
    private val recentMessageDao: RecentMessageDao
) {
    private val tag = this::class.simpleName

    fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        messageAttachmentsDao.getMessageWithAttachments(chatId)

    suspend fun getMessage(messageId: UUID): MessageEntity = messageDao.getMessage(messageId)

    suspend fun insertMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentFormat: AttachmentFormat,
        currentUser: Boolean
    ): MessageEntity {
        return db.withTransaction {
            val chatId = if (currentUser) {
                chatDao.getChatId(recipientId)
                    ?: chatDao.insertChat(ChatEntity(recipientId = recipientId)).toInt()
            } else {
                chatDao.getChatId(senderId)
                    ?: chatDao.insertChat(ChatEntity(recipientId = senderId)).toInt()
            }

            val messageEntity = MessageEntity(
                uid = id,
                senderId = senderId,
                recipientId = recipientId,
                chatId = chatId,
                text = text,
                status = if (currentUser) MessageStatus.SENDING else MessageStatus.RECEIVED,
                createdAt = ZonedDateTime.now(ZoneId.of("UTC"))
            )
            val messageId = messageDao.insertMessage(messageEntity).toInt()
            recentMessageDao.insertRecentMessage(
                RecentMessageEntity(chatId = chatId, messageId = messageId)
            )

            val attachmentEntity = when (attachmentFormat) {
                is AttachmentFormat.UriAttachment -> {
                    val file = attachmentFormat.uri.toFile()
                    val entity = AttachmentEntity(
                        messageId = messageId,
                        fileName = file.name,
                        storageLocation = file.absolutePath,
                        remoteLocation = null,
                        thumbnailLocation = null,
                        type = attachmentFormat.attachmentMetadata.type,
                        width = attachmentFormat.attachmentMetadata.width,
                        height = attachmentFormat.attachmentMetadata.height
                    )
                    attachmentDao.insertAttachment(entity)

                    entity
                }

                is AttachmentFormat.UrlAttachment -> {
                    val networkAttachment = attachmentFormat.networkAttachment
                    val entity = AttachmentEntity(
                        messageId = messageId,
                        fileName = null,
                        storageLocation = null,
                        remoteLocation = networkAttachment.url,
                        thumbnailLocation = null,
                        type = networkAttachment.type,
                        width = networkAttachment.width,
                        height = networkAttachment.height,
                        size = networkAttachment.size
                    )
                    attachmentDao.insertAttachment(entity)

                    entity
                }

                is AttachmentFormat.None -> null
            }

            if (messageEntity.text == null) {
                val messageText = when (attachmentEntity!!.type) {
                    AttachmentType.IMAGE -> "Photo"
                    AttachmentType.VIDEO -> "Video"
                }
                messageDao.updateMessageText(messageId, messageText)
            }

            return@withTransaction messageEntity
        }
    }

    suspend fun markChatMessagesAsSeen(chatId: Int): List<UUID> {
        val unreadMessageIds = messageDao.getUnreadMessagesId(chatId)
        messageDao.markMessagesAsRead(chatId)
        return unreadMessageIds
    }

    suspend fun updateMessageStatus(
        messageId: UUID,
        status: MessageStatus?
    ) {
        if (status == null) {
            Log.w(tag, "Update message status - null status received")
            return
        }

        messageDao.updateMessageStatus(messageId, status)
    }

    suspend fun deleteMessages(ids: List<Int>) {
        messageDao.deleteMessages(ids)
    }
}