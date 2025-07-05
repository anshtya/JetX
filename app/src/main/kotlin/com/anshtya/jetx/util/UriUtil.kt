package com.anshtya.jetx.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

object UriUtil {
    suspend fun Uri.getImageDimensions(context: Context): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        withContext(Dispatchers.IO) {
            ensureActive()
            context.contentResolver.openInputStream(this@getImageDimensions)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        }
        return Pair(options.outHeight, options.outWidth)
    }

    fun Uri.getMimeType(context: Context): String? {
        return if (this.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.getType(this)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }

    fun Uri.getReadableFileSize(): String {
        var size = File(this.path!!).length()
        var readableFileSize = ""
        listOf("B", "KB", "MB").forEach { unit ->
            if (size < 1024) {
                readableFileSize = String.format(Locale.getDefault(), "%d %s", size, unit)
                return readableFileSize
            }
            size /= 1024
        }
        return readableFileSize
    }

    suspend fun Uri.toBitmap(context: Context): Bitmap {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(this)
            .allowHardware(false)
            .build()

        val resultImage = (loader.execute(request) as SuccessResult).image
        return resultImage.toBitmap()
    }
}