package com.anshtya.jetx.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.anshtya.jetx.work.worker.AttachmentDownloadWorker
import com.anshtya.jetx.work.worker.MessageReceiveWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun createMessageReceiveWork(
        messageId: UUID,
        encodedMessage: String
    ) {
        val workRequest = OneTimeWorkRequestBuilder<MessageReceiveWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10L, TimeUnit.SECONDS)
            .setInputData(
                Data.Builder()
                    .putString(MessageReceiveWorker.MESSAGE_KEY, encodedMessage)
                    .build()
            )
            .setId(messageId)
            .build()

        workManager.enqueueUniqueWork(
            MessageReceiveWorker.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun createAttachmentDownloadWork(
        attachmentId: Int,
        messageId: Int
    ) {
        val workRequest = OneTimeWorkRequestBuilder<AttachmentDownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putInt(AttachmentDownloadWorker.ATTACHMENT_ID, attachmentId)
                    .putInt(AttachmentDownloadWorker.MESSAGE_ID, messageId)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "${AttachmentDownloadWorker.WORKER_NAME}-$attachmentId-$messageId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelAttachmentDownloadWork(
        attachmentId: Int,
        messageId: Int
    ) {
        workManager.cancelUniqueWork("${AttachmentDownloadWorker.WORKER_NAME}-$attachmentId-$messageId")
    }
}