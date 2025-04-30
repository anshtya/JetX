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
import com.anshtya.jetx.util.BitmapUtil
import kotlinx.coroutines.flow.Flow
import java.io.File
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
                status = if (currentUser) MessageStatus.SENDING else MessageStatus.RECEIVED
            )
            val messageId = messageDao.upsertMessage(messageEntity)
            when (attachmentFormat) {
                is AttachmentFormat.UriAttachment -> {
                    val file = File(attachmentFormat.uri.path!!)
                    val attachmentDimensions = when (attachmentFormat.type) {
                        AttachmentType.IMAGE -> BitmapUtil.getDimensionsFromUri(attachmentFormat.uri)
                        else -> null
                    }
                    attachmentDao.insertAttachment(
                        AttachmentEntity(
                            messageId = messageId.toInt(),
                            fileName = file.name,
                            storageLocation = file.absolutePath,
                            remoteLocation = null,
                            thumbnailLocation = null,
                            type = attachmentFormat.type,
                            width = attachmentDimensions?.width,
                            height = attachmentDimensions?.height
                        )
                    ).toInt()
                }

                is AttachmentFormat.UrlAttachment -> {
                    val networkAttachment = attachmentFormat.networkAttachment
                    attachmentDao.insertAttachment(
                        AttachmentEntity(
                            messageId = messageId.toInt(),
                            fileName = null,
                            storageLocation = null,
                            remoteLocation = networkAttachment.url,
                            thumbnailLocation = null,
                            type = networkAttachment.type,
                            width = networkAttachment.width,
                            height = networkAttachment.height
                        )
                    ).toInt()
                }

                is AttachmentFormat.None -> null
            }

            if (!currentUser) {
                chatDao.updateUnreadCount(messageEntity.chatId)
            }

            return@withTransaction messageEntity
        }
    }

    suspend fun markChatMessagesAsSeen(chatId: Int): List<UUID> {
        val unreadMessageIds = messageDao.getUnreadMessagesId(chatId)
        db.withTransaction {
            chatDao.markChatAsRead(chatId)
            messageDao.markMessagesAsRead(chatId)
        }
        return unreadMessageIds
    }

    suspend fun updateMessageStatus(messageId: UUID, status: MessageStatus) {
        messageDao.updateMessageStatus(messageId, status)
    }

    suspend fun deleteMessages(ids: List<Int>) {
        messageDao.deleteMessages(ids)
    }
}