package com.anshtya.jetx.attachments

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.anshtya.jetx.common.coroutine.IoDispatcher
import com.anshtya.jetx.util.Constants
import com.anshtya.jetx.util.UriUtil.getDimensions
import com.anshtya.jetx.util.UriUtil.getMimeType
import com.anshtya.jetx.util.UriUtil.getReadableFileSize
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.files.FileNotFoundException
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentManager @Inject constructor(
    client: SupabaseClient,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val imageCompressor: ImageCompressor
) {
    private val tag = this::class.simpleName

    private val mediaBucket = client.storage.from(Constants.MEDIA_STORAGE)
    private val attachmentTable = client.from(Constants.ATTACHMENT_TABLE)

    suspend fun uploadMediaAttachment(uri: Uri): Result<Int> = runCatching {
        val file = File(uri.path!!)

        val inputByteArray = withContext(ioDispatcher) {
            ensureActive()
            file.readBytes()
        }
        val path = file.name

        val attachmentType = AttachmentType.fromMimeType(getMimeTypeFromUri(uri))
        var attachmentHeight: Int? = null
        var attachmentWidth: Int? = null

        when (attachmentType) {
            AttachmentType.IMAGE -> {
                val imageDimensions = uri.getDimensions()
                attachmentHeight = imageDimensions.height
                attachmentWidth = imageDimensions.width
            }

            AttachmentType.VIDEO -> {
                // TODO: get width and height for video
            }

            else -> {}
        }

        mediaBucket.upload(path = path, data = inputByteArray)
        val attachmentUploadResponse = attachmentTable.insert(
            NetworkAttachment(
                url = mediaBucket.publicUrl(path),
                type = attachmentType,
                height = attachmentHeight,
                width = attachmentWidth,
                size = uri.getReadableFileSize()
            )
        ) { select(Columns.list("id")) }.decodeSingle<AttachmentUploadResponse>()

        attachmentUploadResponse.id
    }

    suspend fun saveImage(
        uri: Uri
    ): Result<Uri> = runCatching {
        val mimeType = context.contentResolver.getType(uri)
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        if (fileExtension == null) {
            Log.w("AttachmentManager", "Extension for $mimeType not found")
            throw AttachmentException()
        }

        val compressedByteArray= imageCompressor.compressImage(uri, mimeType).getOrThrow()
        val fileName = "${generateImageName()}.${fileExtension}"

        return saveImage(compressedByteArray, fileName)
    }

    suspend fun saveImage(
        bitmap: Bitmap
    ): Result<Uri> = runCatching {
        val compressedByteArray = imageCompressor.compressImage(bitmap).getOrThrow()
        val fileName = "${generateImageName()}.jpg"
        return saveImage(compressedByteArray, fileName)
    }

    suspend fun saveImage(
        byteArray: ByteArray,
        sent: Boolean = false
    ): Result<Uri> = saveImage(byteArray, "${generateImageName()}.jpg", sent)

    fun getMimeTypeFromUri(uri: Uri): String {
        val mimeType = uri.getMimeType(context)
        if (mimeType == null) {
            Log.w(tag, "Extension for $uri not found")
            throw AttachmentException()
        }
        return mimeType
    }

    private suspend fun saveImage(
        byteArray: ByteArray,
        fileName: String,
        sent: Boolean = true
    ): Result<Uri> = try {
        val directory = File(context.filesDir, if (sent) "Images/Sent" else "Images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val imageFile = File(directory, fileName)
        withContext(ioDispatcher) {
            ensureActive()
            FileOutputStream(imageFile).use { outputStream ->
                outputStream.write(byteArray)
            }
        }
        Result.success(imageFile.toUri())
    } catch (e: FileNotFoundException) {
        Log.w(tag, "${e.message}")
        Result.failure(AttachmentException())
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun generateImageName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        return LocalDateTime.now().format(formatter)
    }
}