package com.anshtya.jetx.util

import android.graphics.Bitmap
import kotlinx.io.IOException
import java.io.ByteArrayOutputStream

object BitmapUtil {
    fun Bitmap.getByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        if (!this.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)) {
            throw IOException("Failed to compress bitmap")
        }
        return outputStream.use { stream -> stream.toByteArray() }
    }
}