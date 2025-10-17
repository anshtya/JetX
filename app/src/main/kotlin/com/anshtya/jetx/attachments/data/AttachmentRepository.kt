package com.anshtya.jetx.attachments.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.OptIn
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.anshtya.jetx.attachments.ImageCompressor
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.util.Constants
import com.anshtya.jetx.util.FileUtil
import com.anshtya.jetx.util.UriUtil.getImageDimensions
import com.anshtya.jetx.util.UriUtil.getMimeType
import com.anshtya.jetx.util.UriUtil.getReadableFileSize
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class AttachmentRepository @Inject constructor(
    client: SupabaseClient,
    @ApplicationContext private val context: Context,
    private val imageCompressor: ImageCompressor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val tag = this::class.simpleName
    private val mediaBucket = client.storage.from(Constants.MEDIA_STORAGE)
    private val attachmentTable = client.from(Constants.ATTACHMENT_TABLE)

    fun getMimeType(
        uri: Uri
    ): String? {
        return uri.getMimeType(context)
    }

    suspend fun getAttachmentMetadata(
        uri: Uri
    ): Result<AttachmentMetadata> = try {
        val mimeType = uri.getMimeType(context)
        if (mimeType == null) {
            throw IllegalArgumentException("Unsupported attachment type")
        }
        val attachmentType = AttachmentType.fromMimeType(mimeType)
        if (attachmentType == null) {
            throw IllegalArgumentException("Unsupported attachment type")
        }

        var attachmentHeight: Int?
        var attachmentWidth: Int?

        when (attachmentType) {
            AttachmentType.IMAGE -> {
                val dimensions = uri.getImageDimensions(context)
                attachmentHeight = dimensions.first
                attachmentWidth = dimensions.second
            }

            AttachmentType.VIDEO -> {
                val metadataRetriever = MediaMetadataRetriever().apply {
                    setDataSource(context, uri)
                }
                attachmentHeight =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        ?.toIntOrNull()
                attachmentWidth =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        ?.toIntOrNull()
            }
        }
        Result.success(
            AttachmentMetadata(
                type = attachmentType,
                height = attachmentHeight,
                width = attachmentWidth
            )
        )
    } catch (e: Exception) {
        Log.e(tag, "Error Attachment metadata - ${e.message}")
        Result.failure(e)
    }

    suspend fun saveAttachmentBeforeUpload(
        uri: Uri
    ): Result<Uri> = try {
        val mimeType = uri.getMimeType(context)
        if (mimeType == null) {
            throw IllegalArgumentException("Unsupported attachment type")
        }

        val attachmentType = AttachmentType.fromMimeType(mimeType)
        val uri = when (attachmentType) {
            AttachmentType.IMAGE -> saveImageBeforeUpload(uri, mimeType).getOrThrow()
            AttachmentType.VIDEO -> saveVideoBeforeUpload(uri).getOrThrow()

            else -> Uri.EMPTY
        }
        Result.success(uri)
    } catch (e: Exception) {
        Log.e(tag, "Error saving attachment before upload - ${e.message}")
        Result.failure(e)
    }

    suspend fun uploadMediaAttachment(
        attachmentPath: String
    ): Result<Int> = try {
        val attachmentFile = File(attachmentPath)

        val attachmentUri = attachmentFile.toUri()
        val mimeType = attachmentUri.getMimeType(context)
        if (mimeType == null) {
            throw IllegalArgumentException("Unsupported attachment type")
        }

        val attachmentMetadata = getAttachmentMetadata(attachmentUri).getOrThrow()

        val inputByteArray = withContext(ioDispatcher) {
            ensureActive()
            attachmentFile.readBytes()
        }
        val name = attachmentFile.name

        mediaBucket.upload(path = name, data = inputByteArray)
        val attachmentUploadResponse = attachmentTable.insert(
            NetworkAttachment(
                url = mediaBucket.publicUrl(name),
                type = attachmentMetadata.type,
                height = attachmentMetadata.height,
                width = attachmentMetadata.width,
                size = attachmentUri.getReadableFileSize()
            )
        ) { select(Columns.list("id")) }.decodeSingle<AttachmentUploadResponse>()

        Result.success(attachmentUploadResponse.id)
    } catch (e: Exception) {
        Log.e(tag, "Error uploading attachment - ${e.message}")
        Result.failure(e)
    }

    fun migrateToStorage(
        uri: Uri
    ): Result<Uri> = try {
        val storageDirectory = FileUtil.getAttachmentStorageDirectory(context)
        val attachmentFile = uri.toFile()
        val newFile = File(storageDirectory, attachmentFile.name)
        with(attachmentFile) {
            copyTo(newFile, overwrite = true)
            delete()
        }
        Result.success(newFile.toUri())
    } catch (e: Exception) {
        Log.e(tag, "Error saving attachment to storage - ${e.message}")
        Result.failure(e)
    }

    suspend fun saveImage(
        byteArray: ByteArray
    ): Result<Uri> = try {
        val storageDirectory = FileUtil.getAttachmentStorageDirectory(context)
        val file = FileUtil.createFile(storageDirectory, ext = "jpg")
        withContext(ioDispatcher) {
            ensureActive()
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
        }
        Result.success(file.toUri())
    } catch (e: Exception) {
        Log.e(tag, "Error saving image to storage - ${e.message}")
        Result.failure(e)
    }

    suspend fun saveVideo(
        byteArray: ByteArray
    ): Result<Uri> = try {
        val storageDirectory = FileUtil.getAttachmentStorageDirectory(context)
        val file = FileUtil.createFile(storageDirectory, ext = "mp4")
        withContext(ioDispatcher) {
            ensureActive()
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
        }
        Result.success(file.toUri())
    } catch (e: Exception) {
        Log.e(tag, "Error saving video to storage - ${e.message}")
        Result.failure(e)
    }

    private suspend fun saveImageBeforeUpload(
        uri: Uri,
        mimeType: String
    ): Result<Uri> = try {
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        val byteArray = imageCompressor.compressImage(uri, mimeType).getOrThrow()
        withContext(ioDispatcher) {
            val fileDirectory = FileUtil.getAttachmentCacheDirectory(context)
            val outputPath = FileUtil.createFile(fileDirectory, ext = fileExtension)
            ensureActive()
            FileOutputStream(outputPath).use { outputStream -> outputStream.write(byteArray) }
            Result.success(outputPath.toUri())
        }
    } catch (e: Exception) {
        Log.e(tag, "Error saving image before upload - ${e.message}")
        Result.failure(e)
    }

    suspend fun saveBitmapImageBeforeUpload(
        bitmap: Bitmap
    ): Result<Uri> = try {
        val byteArray = imageCompressor.compressImage(bitmap).getOrThrow()
        withContext(ioDispatcher) {
            val fileDirectory = FileUtil.getAttachmentCacheDirectory(context)
            val outputPath = FileUtil.createFile(fileDirectory, ext = "jpg")
            ensureActive()
            FileOutputStream(outputPath).use { outputStream -> outputStream.write(byteArray) }
            Result.success(outputPath.toUri())
        }
    } catch (e: Exception) {
        Log.e(tag, "Error saving bitmap before upload - ${e.message}")
        Result.failure(e)
    }

    @OptIn(UnstableApi::class)
    private suspend fun saveVideoBeforeUpload(
        uri: Uri
    ): Result<Uri> {
        return try {
            val outputPath = withContext(ioDispatcher) {
                val fileDirectory = FileUtil.getAttachmentCacheDirectory(context)
                FileUtil.createFile(fileDirectory, ext = "mp4")
            }

            val metadata = getAttachmentMetadata(uri).getOrThrow()
            if (metadata.height!! <= 480 || metadata.width!! <= 480) {
                val file = File(uri.path!!)
                with(file) {
                    copyTo(outputPath, overwrite = true)

                    /**
                     * If the video URI is not from a content provider, it likely refers to a
                     * file saved directly by the app (e.g., via CameraX to cache directory).
                     * In that case, it's safe to delete the file directly.
                     */
                    if (uri.scheme != ContentResolver.SCHEME_CONTENT) delete()
                }
                return Result.success(outputPath.toUri())
            }

            val mediaItem = MediaItem.fromUri(uri)
            val videoProcessor = Presentation.createForHeight(480)
            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setEffects(Effects(listOf(), listOf(videoProcessor)))
                .build()

            val transformer = Transformer.Builder(context).build()

            val uri = suspendCancellableCoroutine { continuation ->
                val transformerListener = object : Transformer.Listener {
                    override fun onCompleted(
                        composition: Composition,
                        result: ExportResult
                    ) {
                        Log.i(tag, "Transformer completed ${outputPath.toUri()}")
                        continuation.resume(outputPath.toUri()) { _, _, _ -> transformer.cancel() }
                    }

                    override fun onError(
                        composition: Composition,
                        result: ExportResult,
                        exception: ExportException
                    ) {
                        Log.e(tag, "Transformer exception - ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
                }

                transformer.addListener(transformerListener)
                transformer.start(editedMediaItem, outputPath.path)
            }

            Result.success(uri)
        } catch (e: Exception) {
            Log.e(tag, "error in transformer - ${e.message}")
            Result.failure(e)
        }
    }
}