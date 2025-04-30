package com.anshtya.jetx.util

import android.graphics.BitmapFactory
import android.net.Uri
import com.anshtya.jetx.util.model.ImageDimensions
import java.io.File

object BitmapUtil {
    fun getDimensionsFromUri(uri: Uri): ImageDimensions {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(File(uri.path!!).absolutePath, options)
        return ImageDimensions(
            width = options.outWidth,
            height = options.outHeight
        )
    }
}