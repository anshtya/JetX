package com.anshtya.jetx.work

import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import com.anshtya.jetx.work.worker.AttachmentDownloadWorker
import com.anshtya.jetx.work.worker.MessageReceiveWorker
import com.anshtya.jetx.work.worker.MessageSendWorker
import java.util.UUID
import javax.inject.Inject

class WorkScheduler @Inject constructor(
    private val workManagerHelper: WorkManagerHelper
) {
    fun createMessageReceiveWork(
        encodedMessage: String
    ) {
        workManagerHelper.scheduleOneTimeWork(
            workerClass = MessageReceiveWorker::class,
            dataParams = mapOf(MessageReceiveWorker.MESSAGE_KEY to encodedMessage),
            uniqueWorkName = MessageReceiveWorker.WORKER_NAME,
            existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE,
            backoffPolicy = BackoffPolicy.LINEAR
        )
    }

    fun createMessageSendWork(
        messageId: UUID
    ) {
        workManagerHelper.scheduleOneTimeWork(
            workerClass = MessageSendWorker::class,
            dataParams = mapOf(MessageSendWorker.MESSAGE_ID_KEY to messageId.toString()),
            uniqueWorkName = MessageSendWorker.WORKER_NAME,
            existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE
        )
    }

    fun createAttachmentDownloadWork(
        attachmentId: Int,
        messageId: Int
    ) {
        workManagerHelper.scheduleOneTimeWork(
            workerClass = AttachmentDownloadWorker::class,
            dataParams = mapOf(
                AttachmentDownloadWorker.ATTACHMENT_ID to attachmentId,
                AttachmentDownloadWorker.MESSAGE_ID to messageId,
            ),
            uniqueWorkName = AttachmentDownloadWorker.generateWorkerName(attachmentId, messageId),
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            constraints = workManagerHelper.buildConstraints(requiresBatteryNotLow = true)
        )
    }
}