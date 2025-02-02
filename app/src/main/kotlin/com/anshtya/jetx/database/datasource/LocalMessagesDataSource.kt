package com.anshtya.jetx.database.datasource

import androidx.room.withTransaction
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
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentUri: String?,
        currentUser: Boolean
    ): MessageEntity {
        return db.withTransaction {
            val chatId = if (currentUser) {
                chatDao.getChat(recipientId)?.id
                    ?: chatDao.insertChat(ChatEntity(recipientId = recipientId)).toInt()
            } else {
                chatDao.getChat(senderId)?.id
                    ?: chatDao.insertChat(ChatEntity(recipientId = senderId)).toInt()
            }

            val messageEntity = MessageEntity(
                id = id,
                senderId = senderId,
                recipientId = recipientId,
                chatId = chatId,
                text = text,
                attachmentUri = attachmentUri,
                status = if (currentUser) MessageStatus.SENDING else MessageStatus.RECEIVED
            )
            messageDao.upsertMessage(messageEntity)
            if (!currentUser) {
                chatDao.updateUnreadCount(messageEntity.chatId)
            }

            return@withTransaction messageEntity
        }
    }

    suspend fun updateMessage(messageEntity: MessageEntity) {
        messageDao.upsertMessage(messageEntity)
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
}