package com.anshtya.jetx.shared.attachments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import com.anshtya.jetx.shared.util.UriUtil.getMimeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

actual class ImageCompressor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher
) {
    private val tag = this::class.simpleName

    actual suspend fun compressImage(
        imagePath: String
    ): Result<ByteArray> = runCatching {
        val uri = imagePath.toUri()
        val mimeType = uri.getMimeType(context)

        val inputBytes = withContext(ioDispatcher) {
            ensureActive()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        }
        if (inputBytes == null) {
            Log.i(tag, "Input stream is null for URI: $uri")
            throw IOException("File doesn't exist")
        }

        return withContext(defaultDispatcher) {
            ensureActive()
            val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
            if (bitmap == null) {
                throw IOException("Image could not be decoded")
            }

            val compressFormat = when (mimeType) {
                "image/png" -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG
            }

            compressBitmap(bitmap, compressFormat)
        }
    }

    actual suspend fun compressImage(
        image: Image
    ): Result<ByteArray> = compressBitmap(image)

    private suspend fun compressBitmap(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Result<ByteArray> = runCatching {
        withContext(defaultDispatcher) {
            ensureActive()
            return@withContext ByteArrayOutputStream().use { outputStream ->
                val compressionSuccess = bitmap.compress(compressFormat, 80, outputStream)
                if (!compressionSuccess) {
                    throw IOException("Bitmap compression failed")
                }
                outputStream.toByteArray()
            }
        }
    }
}