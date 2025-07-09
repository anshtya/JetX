package com.anshtya.jetx.work.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.shared.chats.toNetworkMessage
import com.anshtya.jetx.shared.database.dao.AttachmentDao
import com.anshtya.jetx.shared.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.shared.model.MessageStatus
import com.anshtya.jetx.util.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.koin.android.annotation.KoinWorker
import java.util.UUID

@KoinWorker
class MessageSendWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val client: SupabaseClient,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentDao: AttachmentDao,
    private val localMessagesDataSource: LocalMessagesDataSource
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val messageIdString = inputData.getString(MESSAGE_ID_KEY) ?: return Result.failure()

            val messagesTable = client.from(Constants.MESSAGE_TABLE)

            val message = localMessagesDataSource.getMessage(UUID.fromString(messageIdString))
            val attachmentLocation = attachmentDao.getStorageLocationForAttachment(message.id)

            val attachmentId = if (attachmentLocation != null) {
                attachmentRepository.uploadMediaAttachment(attachmentLocation).getOrThrow()
            } else null

            messagesTable.insert(message.toNetworkMessage(attachmentId))
            localMessagesDataSource.updateMessageStatus(
                message.uid, message.chatId, MessageStatus.SENT
            )

            Result.success()
        } catch (e: Exception) {
            Log.w("MessageSendWorker", "${e.message}")
            Result.retry()
        }
    }

    companion object {
        const val WORKER_NAME = "message_send"
        const val MESSAGE_ID_KEY = "message_id_key"
    }
}