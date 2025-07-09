package com.anshtya.jetx.work.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.shared.attachments.AttachmentTransferState
import com.anshtya.jetx.shared.attachments.AttachmentType
import com.anshtya.jetx.shared.database.dao.AttachmentDao
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.annotation.KoinWorker
import java.io.File

@KoinWorker
class AttachmentDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentDao: AttachmentDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val attachmentId = inputData.getInt(ATTACHMENT_ID, 0).takeIf { it > 0 }
            ?: return Result.failure()
        val messageId = inputData.getInt(MESSAGE_ID, 0).takeIf { it > 0 }
            ?: return Result.failure()

        val fileUrl = attachmentDao.getRemoteUrlForAttachment(attachmentId, messageId)
        val attachmentType = attachmentDao.getAttachmentType(attachmentId, messageId)
        attachmentDao.updateAttachmentTransferState(
            attachmentId, messageId, AttachmentTransferState.STARTED
        )

        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(fileUrl).build()
            val response = client.newCall(request).execute()

            val fileUri = when (attachmentType) {
                AttachmentType.IMAGE -> attachmentRepository.saveImage(response.body!!.bytes())
                    .getOrThrow()

                AttachmentType.VIDEO -> attachmentRepository.saveVideo(response.body!!.bytes())
                    .getOrThrow()
            }
            attachmentDao.updateAttachmentDownloadAsFinished(
                attachmentId, messageId, File(fileUri.path!!).absolutePath
            )
            Result.success()
        } catch (e: Exception) {
            attachmentDao.updateAttachmentTransferState(
                attachmentId, messageId, AttachmentTransferState.FAILED
            )
            Log.w("AttachmentDownloadWorker", "${e.message}")
            Result.failure()
        }
    }

    companion object {
        const val WORKER_NAME = "attachment_download"
        const val ATTACHMENT_ID = "attachment_id"
        const val MESSAGE_ID = "message_id"

        fun generateWorkerName(
            attachmentId: Int,
            messageId: Int
        ): String = "${WORKER_NAME}-$attachmentId-$messageId"
    }
}