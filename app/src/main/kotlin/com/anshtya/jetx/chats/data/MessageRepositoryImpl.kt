package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.MessageInsertData
import com.anshtya.jetx.chats.data.model.toNetworkMessage
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.toExternalModel
import com.anshtya.jetx.util.Constants.FULL_DATE
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import com.anshtya.jetx.util.getDateOrTime
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val messageDao: MessageDao
) : MessageRepository {
    private val userId: String = client.auth.currentSessionOrNull()?.user?.id
        ?: throw IllegalStateException("User should be logged in to access messages")
    private val messagesTable = client.from(MESSAGE_TABLE)

    override fun getChatMessages(chatId: Int): Flow<DateChatMessages> {
        return messageDao.getChatMessages(chatId)
            .map { messages ->
                DateChatMessages(
                    messages = messages.groupBy(
                        keySelector = { message -> message.createdAt.truncatedTo(ChronoUnit.DAYS) },
                        valueTransform = { message -> message.toExternalModel() }
                    ).mapKeys { (createdAt, _) ->
                        createdAt.getDateOrTime(
                            datePattern = FULL_DATE,
                            getToday = true,
                            getYesterday = true
                        )
                    }
                )
            }
    }

    override suspend fun insertMessage(messageInsertData: MessageInsertData) {
        // TODO: add WorkManager if send message fails
        val messageEntity = MessageEntity(
            id = UUID.randomUUID(),
            senderId = UUID.fromString(userId),
            recipientId = messageInsertData.recipientId,
            chatId = messageInsertData.chatId,
            text = messageInsertData.message,
            attachmentUri = messageInsertData.attachment
        )
        messageDao.upsertMessage(messageEntity)
        messagesTable.insert(messageEntity.toNetworkMessage())
    }
}