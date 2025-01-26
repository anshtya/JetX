package com.anshtya.jetx.database.datasource

import androidx.room.withTransaction
import com.anshtya.jetx.common.model.IncomingMessage
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.JetXDatabase
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class LocalMessagesDataSource @Inject constructor(
    private val db: JetXDatabase,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao
) {
    fun getChatMessages(chatId: Int): Flow<List<MessageEntity>> = messageDao.getChatMessages(chatId)

    suspend fun getChatMessage(id: UUID): MessageEntity = messageDao.getChatMessage(id)

    suspend fun insertMessage(
        incomingMessage: IncomingMessage,
        isCurrentUser: Boolean
    ) {
        db.withTransaction {
            val chatId = if (isCurrentUser) {
                chatDao.getChat(incomingMessage.recipientId)?.id
                    ?: chatDao.insertChat(ChatEntity(recipientId = incomingMessage.recipientId))
                    .toInt()
            } else {
                chatDao.getChat(incomingMessage.senderId)?.id
                    ?: chatDao.insertChat(ChatEntity(recipientId = incomingMessage.senderId))
                        .toInt()
            }

            val messageEntity = MessageEntity(
                id = incomingMessage.id,
                senderId = incomingMessage.senderId,
                recipientId = incomingMessage.recipientId,
                chatId = chatId,
                text = incomingMessage.text,
                attachmentUri = incomingMessage.attachmentUri,
                status = incomingMessage.status
            )
            messageDao.upsertMessage(messageEntity)
            if (!isCurrentUser) {
                chatDao.updateUnreadCount(messageEntity.chatId)
            }
        }
    }

    suspend fun updateMessage(messageEntity: MessageEntity) {
        messageDao.upsertMessage(messageEntity)
    }

    suspend fun markChatAsRead(recipientId: UUID): List<UUID>? {
        val chatId = chatDao.getChat(recipientId)?.id ?: return null

        val unreadMessageIds = messageDao.getUnreadMessagesId(chatId, recipientId)
        if (messageDao.getRecentUnreadChatMessage(chatId, recipientId)) {
            db.withTransaction {
                chatDao.markChatAsRead(chatId)
                messageDao.markMessagesAsRead(chatId, recipientId)
            }
        }
        return unreadMessageIds
    }

    suspend fun markMessageSeen(messageId: UUID) {
        val message = messageDao.getChatMessage(messageId)
        db.withTransaction {
            messageDao.updateMessageStatus(messageId, MessageStatus.SEEN)
            chatDao.markChatAsRead(message.chatId)
        }
    }

    suspend fun updateMessageStatus(messageId: UUID, status: MessageStatus) {
        messageDao.updateMessageStatus(messageId, status)
    }
}