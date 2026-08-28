package com.anshtya.jetx.attachments.data

import android.graphics.Bitmap
import android.net.Uri
import com.anshtya.jetx.core.network.model.NetworkAttachment
import java.util.UUID

interface AttachmentRepository {
    fun getMimeType(uri: Uri): String?

    suspend fun getAttachmentMetadata(uri: Uri): Result<AttachmentMetadata>

    suspend fun getAttachment(id: UUID): Result<NetworkAttachment>

    suspend fun saveAttachmentBeforeUpload(uri: Uri): Result<Uri>

    suspend fun uploadMediaAttachment(attachmentPath: String): Result<UUID>

    fun migrateToStorage(uri: Uri): Result<Uri>

    suspend fun saveImage(byteArray: ByteArray): Result<Uri>

    suspend fun saveVideo(byteArray: ByteArray): Result<Uri>

    suspend fun saveBitmapImageBeforeUpload(bitmap: Bitmap): Result<Uri>
}
