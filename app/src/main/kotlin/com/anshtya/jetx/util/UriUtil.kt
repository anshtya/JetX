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
import com.anshtya.jetx.util.model.ImageDimensions
import java.io.File

object UriUtil {
    fun Uri.getDimensions(): ImageDimensions {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(File(this.path!!).absolutePath, options)
        return ImageDimensions(
            width = options.outWidth,
            height = options.outHeight
        )
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
        val file = File(this.path!!)
        return getReadableFileSize(file.length())
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