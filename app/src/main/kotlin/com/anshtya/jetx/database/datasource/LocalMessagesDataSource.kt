package com.anshtya.jetx.database.datasource

import androidx.room.withTransaction
import com.anshtya.jetx.attachments.AttachmentFormat
import com.anshtya.jetx.attachments.AttachmentType
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.JetXDatabase
import com.anshtya.jetx.database.dao.AttachmentDao
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.entity.AttachmentEntity
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.model.MessageWithAttachment
import com.anshtya.jetx.util.UriUtil.getDimensions
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class LocalMessagesDataSource @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val chatDao: ChatDao,
    private val db: JetXDatabase,
    private val messageDao: MessageDao,
    private val messageAttachmentsDao: MessageAttachmentsDao
) {
    fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>> =
        messageAttachmentsDao.getMessageWithAttachments(chatId)

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
            val messageId = messageDao.insertMessage(messageEntity)
            val attachmentEntity = when (attachmentFormat) {
                is AttachmentFormat.UriAttachment -> {
                    val file = File(attachmentFormat.uri.path!!)
                    val attachmentDimensions = when (attachmentFormat.type) {
                        AttachmentType.IMAGE -> attachmentFormat.uri.getDimensions()
                        else -> null
                    }
                    val entity = AttachmentEntity(
                        messageId = messageId.toInt(),
                        fileName = file.name,
                        storageLocation = file.absolutePath,
                        remoteLocation = null,
                        thumbnailLocation = null,
                        type = attachmentFormat.type,
                        width = attachmentDimensions?.width,
                        height = attachmentDimensions?.height
                    )
                    attachmentDao.insertAttachment(entity)

                    entity
                }

                is AttachmentFormat.UrlAttachment -> {
                    val networkAttachment = attachmentFormat.networkAttachment
                    val entity = AttachmentEntity(
                        messageId = messageId.toInt(),
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

            val messageText = messageEntity.text ?: when (attachmentEntity!!.type) {
                AttachmentType.IMAGE -> "Photo"
                AttachmentType.VIDEO -> "Video"
                AttachmentType.DOCUMENT -> "Document"
            }
            chatDao.updateRecentMessage(
                chatId = chatId,
                senderId = messageEntity.senderId,
                messageText = messageText,
                messageTimestamp = messageEntity.createdAt,
                messageStatus = messageEntity.status
            )
            if (!currentUser) {
                chatDao.updateUnreadCount(messageEntity.chatId)
            }

            return@withTransaction messageEntity
        }
    }

    suspend fun markChatMessagesAsSeen(chatId: Int): List<UUID> {
        val unreadMessageIds = messageDao.getUnreadMessagesId(chatId)
        db.withTransaction {
            messageDao.markMessagesAsRead(chatId)
            chatDao.markChatAsRead(chatId)
        }
        return unreadMessageIds
    }

    suspend fun updateMessageStatus(
        messageId: UUID,
        messageChatId: Int,
        status: MessageStatus
    ) {
        db.withTransaction {
            messageDao.updateMessageStatus(messageId, status)
            chatDao.updateRecentMessageStatus(messageChatId, status)
        }
    }

    suspend fun updateMessage(
        messageId: UUID,
        text: String?,
        status: MessageStatus?
    ) {
        db.withTransaction {
            if (text != null && status != null) {
                messageDao.updateMessage(messageId, text, status)
                chatDao.updateRecentMessageTextAndStatus(
                    chatId = messageDao.getMessageChatId(messageId),
                    messageText = text,
                    messageStatus = status
                )
            } else if (status != null) {
                updateMessageStatus(
                    messageId = messageId,
                    messageChatId = messageDao.getMessageChatId(messageId),
                    status = status
                )
            }
        }
    }

    suspend fun deleteMessages(ids: List<Int>) {
        messageDao.deleteMessages(ids)
    }
}