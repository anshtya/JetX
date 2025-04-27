package com.anshtya.jetx.attachments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageCompressor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher
) {
    suspend fun compressImage(
        uri: Uri,
        mimeType: String?
    ): ByteArray? {
        return withContext(ioDispatcher) {
            val inputBytes = context.contentResolver
                .openInputStream(uri)?.use { inputStream -> inputStream.readBytes() }
                ?: return@withContext null

            withContext(defaultDispatcher) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                val compressFormat = when (mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }
                return@withContext compressBitmap(bitmap, compressFormat)
            }
        }
    }

    suspend fun compressImage(
        bitmap: Bitmap
    ): ByteArray? {
        return compressBitmap(bitmap)
    }

    private suspend fun compressBitmap(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): ByteArray? {
        return withContext(defaultDispatcher) {
            return@withContext ByteArrayOutputStream().use { outputStream ->
                bitmap.compress(compressFormat, 70, outputStream)
                outputStream.toByteArray()
            }
        }
    }
}