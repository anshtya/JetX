package com.anshtya.jetx.attachments

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.anshtya.jetx.common.coroutine.IoDispatcher
import com.anshtya.jetx.common.model.Result
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
    private val mediaBucket = client.storage.from(Constants.MEDIA_STORAGE)
    private val attachmentTable = client.from(Constants.ATTACHMENT_TABLE)

    suspend fun uploadMediaAttachment(uri: Uri): Int {
        val file = File(uri.path!!)
        val inputByteArray = withContext(ioDispatcher) { file.readBytes() }
        val path = file.name
        mediaBucket.upload(path = path, data = inputByteArray)
        val attachmentUploadResponse = attachmentTable.insert(
            NetworkAttachmentUpload(
                url = mediaBucket.publicUrl(path),
                type = AttachmentType.fromMimeType(getMimeTypeFromUri(context, uri))!!
            )
        ) { select(Columns.list("id")) }.decodeSingle<NetworkAttachmentUploadResponse>()
        return attachmentUploadResponse.id
    }

    suspend fun saveImage(
        uri: Uri
    ): Result<Uri> {
        return try {
            val mimeType = context.contentResolver.getType(uri)
            val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            val compressedByteArray = imageCompressor.compressImage(uri, mimeType)
            val fileName = "${generateImageName()}.${fileExtension}"
            return Result.Success(saveImage(compressedByteArray, fileName))
        } catch (_: Exception) {
            Result.Error()
        }
    }

    suspend fun saveImage(
        bitmap: Bitmap
    ): Result<Uri> {
        return try {
            val compressedByteArray = imageCompressor.compressImage(bitmap)
            val fileName = "${generateImageName()}.jpg"
            return Result.Success(saveImage(compressedByteArray, fileName))
        } catch (_: Exception) {
            Result.Error()
        }
    }

    suspend fun saveImage(
        byteArray: ByteArray,
        sent: Boolean = false
    ): Uri {
        return saveImage(byteArray, "${generateImageName()}.jpg", sent)
    }

    // TODO: save image in chunks
    suspend fun saveImageInChunks(
        buffer: ByteArray,
        offset: Int,
        length: Int,
        fileUri: Uri? = null
    ): Uri {
        val imageFile = if (fileUri != null) {
            File(fileUri.path!!)
        } else {
            val fileName = "${generateImageName()}.jpg"
            val directory = File(context.filesDir, "Images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            File(directory, fileName)
        }
        withContext(ioDispatcher) {
            FileOutputStream(imageFile).use { outputStream ->
                outputStream.write(buffer, offset, length)
            }
        }
        return imageFile.toUri()
    }

    fun getMimeTypeFromUri(uri: Uri): String? {
        return getMimeTypeFromUri(context, uri)
    }

    private suspend fun saveImage(
        byteArray: ByteArray?,
        fileName: String,
        sent: Boolean = true
    ): Uri {
        val directory = File(context.filesDir, if (sent) "Images/Sent" else "Images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val imageFile = File(directory, fileName)
        withContext(ioDispatcher) {
            FileOutputStream(imageFile).use { outputStream ->
                outputStream.write(byteArray)
            }
        }
        return imageFile.toUri()
    }

    private fun getMimeTypeFromUri(context: Context, uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }

    private fun generateImageName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        return LocalDateTime.now().format(formatter)
    }
}