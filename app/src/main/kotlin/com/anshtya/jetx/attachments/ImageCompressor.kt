package com.anshtya.jetx.attachments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.anshtya.jetx.core.coroutine.DefaultDispatcher
import com.anshtya.jetx.core.coroutine.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    private val tag = this::class.simpleName

    suspend fun compressImage(
        uri: Uri,
        mimeType: String?
    ): Result<ByteArray> = runCatching {
        val inputBytes = withContext(ioDispatcher) {
            ensureActive()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        }
        if (inputBytes == null){
            Log.i(tag, "Input stream is null for URI: $uri")
            throw IOException("File doesn't exist")
        }

        return withContext(defaultDispatcher) {
            ensureActive()
            val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                ?: throw IOException("Image could not be decoded")

            val compressFormat = when (mimeType) {
                "image/png" -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG
            }

            compressBitmap(bitmap, compressFormat)
        }
    }

    suspend fun compressImage(bitmap: Bitmap): Result<ByteArray> = compressBitmap(bitmap)

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