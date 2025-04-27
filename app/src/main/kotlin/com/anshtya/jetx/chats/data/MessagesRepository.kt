package com.anshtya.jetx.chats.data

import android.net.Uri
import com.anshtya.jetx.database.model.MessageWithAttachment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MessagesRepository {
    fun getChatMessages(chatId: Int): Flow<List<MessageWithAttachment>>

    suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?,
        attachmentUri: Uri?
    )

    suspend fun sendChatMessage(
        chatId: Int,
        text: String,
    )

    suspend fun markChatMessagesAsSeen(chatId: Int)

    suspend fun deleteMessages(ids: List<Int>)
}