package com.anshtya.jetx.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
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
}