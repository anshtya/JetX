package com.anshtya.jetx.work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anshtya.jetx.attachments.AttachmentManager
import com.anshtya.jetx.database.model.AttachmentTransferState
import com.anshtya.jetx.common.coroutine.IoDispatcher
import com.anshtya.jetx.database.dao.AttachmentDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@HiltWorker
class AttachmentDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val attachmentManager: AttachmentManager,
    private val attachmentDao: AttachmentDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val attachmentId = inputData.getInt(ATTACHMENT_ID, 0).takeIf { it > 0 }
            ?: return@withContext Result.failure()
        val messageId = inputData.getInt(MESSAGE_ID, 0).takeIf { it > 0 }
            ?: return@withContext Result.failure()

        val fileUrl = attachmentDao.getRemoteUrlForAttachment(attachmentId, messageId)
        attachmentDao.updateAttachmentTransferState(
            attachmentId, messageId, AttachmentTransferState.STARTED
        )
        return@withContext try {
            val client = OkHttpClient()
            val request = Request.Builder().url(fileUrl).build()
            val response = client.newCall(request).execute()

            val fileUri = attachmentManager.saveImage(response.body!!.bytes())
            attachmentDao.updateAttachmentDownloadAsFinished(
                attachmentId, messageId, File(fileUri.path!!).absolutePath
            )
            Result.success()
        } catch (e: Exception) {
            attachmentDao.updateAttachmentTransferState(
                attachmentId, messageId, AttachmentTransferState.FAILED
            )
            Log.e("AttachmentDownloadWorker", "$e")
            Result.failure()
        }
    }

    companion object {
        const val WORKER_NAME = "attachment_download"
        const val ATTACHMENT_ID = "attachment_id"
        const val MESSAGE_ID = "message_id"
    }
}