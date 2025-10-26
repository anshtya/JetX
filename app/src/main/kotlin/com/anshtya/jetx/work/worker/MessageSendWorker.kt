package com.anshtya.jetx.work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.core.database.dao.AttachmentDao
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.core.database.model.MessageStatus
import com.anshtya.jetx.core.model.MessageType
import com.anshtya.jetx.core.network.service.MessageService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.work.util.createInputData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MessageSendWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val messageService: MessageService,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentDao: AttachmentDao,
    private val localMessagesDataSource: LocalMessagesDataSource
) : CoroutineWorker(appContext, workerParams) {
    private val tag = this::class.simpleName

    override suspend fun doWork(): Result {
        return try {
            val messageId = inputData.getInt(MESSAGE_ID_KEY, 0)
            if (messageId == 0) return Result.failure()

            val message = localMessagesDataSource.getMessage(messageId)
            val attachmentLocation = attachmentDao.getStorageLocationForAttachment(message.id)

            val attachmentId = if (attachmentLocation != null) {
                attachmentRepository.uploadMediaAttachment(attachmentLocation).getOrThrow()
            } else null

            messageService.sendMessage(
                id = message.uid,
                senderId = message.senderId,
                type = MessageType.INDIVIDUAL,
                targetId = message.recipientId,
                content = message.text,
                attachmentId = attachmentId
            ).toResult().getOrThrow()
            localMessagesDataSource.updateMessageStatus(message.uid, MessageStatus.SENT)

            Result.success()
        } catch (e: Exception) {
            Log.w(tag, e.message, e)
            Result.retry()
        }
    }

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun scheduleWork(
            workManager: WorkManager,
            messageId: Int
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(MessageSendWorker::class)
                .setInputData(createInputData(mapOf(MESSAGE_ID_KEY to messageId)))
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                "message_send_$messageId",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )
        }
    }
}