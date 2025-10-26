package com.anshtya.jetx.work.worker

import android.content.Context
import android.net.Uri
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
import com.anshtya.jetx.attachments.data.AttachmentType
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.database.dao.AttachmentDao
import com.anshtya.jetx.core.database.model.AttachmentTransferState
import com.anshtya.jetx.core.network.service.StorageService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.s3.S3
import com.anshtya.jetx.work.util.createInputData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class AttachmentDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentDao: AttachmentDao,
    private val storageService: StorageService,
    private val s3: S3,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams) {
    private val tag = this::class.simpleName

    override suspend fun doWork(): Result {
        val attachmentId = inputData.getInt(ATTACHMENT_ID, 0).takeIf { it > 0 }
            ?: return Result.failure()
        val messageId = inputData.getInt(MESSAGE_ID, 0).takeIf { it > 0 }
            ?: return Result.failure()

        val attachmentName = attachmentDao.getRemoteLocation(attachmentId)
        attachmentDao.updateAttachmentTransferState(
            attachmentId, messageId, AttachmentTransferState.STARTED
        )

        return try {
            val url = storageService.getFileDownloadUrl(attachmentName)
                .toResult().getOrThrow().url
            val downloadedFile = s3.download(url).getOrThrow()

            val attachmentType = AttachmentType.fromMimeType(downloadedFile.mimeType)
            val byteArray = withContext(ioDispatcher) {
                downloadedFile.bytes.use { it.readBytes() }
            }

            val fileUri = when (attachmentType) {
                AttachmentType.IMAGE -> attachmentRepository.saveImage(byteArray).getOrThrow()
                AttachmentType.VIDEO -> attachmentRepository.saveVideo(byteArray).getOrThrow()

                else -> Uri.EMPTY
            }
            attachmentDao.updateAttachmentDownloadAsFinished(
                attachmentId, messageId, File(fileUri.path!!).absolutePath
            )
            Result.success()
        } catch (e: Exception) {
            attachmentDao.updateAttachmentTransferState(
                attachmentId, messageId, AttachmentTransferState.FAILED
            )
            Log.w(tag, "${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        const val WORKER_NAME = "attachment_download"
        const val ATTACHMENT_ID = "attachment_id"
        const val MESSAGE_ID = "message_id"

        private fun generateWorkerName(
            attachmentId: Int,
            messageId: Int
        ): String = "${WORKER_NAME}-$attachmentId-$messageId"

        fun scheduleWork(
            workManager: WorkManager,
            attachmentId: Int,
            messageId: Int
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(AttachmentDownloadWorker::class)
                .setInputData(
                    createInputData(mapOf(ATTACHMENT_ID to attachmentId, MESSAGE_ID to messageId))
                )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                generateWorkerName(attachmentId, messageId),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelWork(
            workManager: WorkManager,
            attachmentId: Int,
            messageId: Int
        ) {
            workManager.cancelUniqueWork(generateWorkerName(attachmentId, messageId))
        }
    }
}