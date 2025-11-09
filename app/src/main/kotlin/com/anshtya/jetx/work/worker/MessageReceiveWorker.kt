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
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.core.database.dao.MessageDao
import com.anshtya.jetx.core.network.service.MessageService
import com.anshtya.jetx.core.network.util.toResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MessageReceiveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val messageService: MessageService,
    private val messagesRepository: MessagesRepository,
    private val messageDao: MessageDao,
) : CoroutineWorker(appContext, workerParams) {
    private val tag = this::class.simpleName

    override suspend fun doWork(): Result {
        return try {
            val pendingMessages = messageService.getPendingMessages()
                .toResult()
                .getOrElse {
                    Log.e(tag, "Failed to retrieve messages", it)
                    return Result.retry()
                }

            pendingMessages.forEach { message ->
                val messageExists = messageDao.messageExists(message.id)
                if (messageExists) {
                    val savedMessage = messageDao.getMessage(message.id)
                        .copy(
                            text = message.content,
                            status = message.status!!
                        )
                    messageDao.insertMessage(savedMessage)
                } else {
                    messagesRepository.receiveChatMessage(
                        id = message.id,
                        senderId = message.senderId,
                        recipientId = message.targetId,
                        text = message.content,
                        attachmentId = message.attachmentId
                    ).getOrThrow()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.w(tag, e.message, e)
            Result.failure()
        }
    }

    companion object {
        fun scheduleWork(
            workManager: WorkManager,
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(MessageReceiveWorker::class)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                "message_receive",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}