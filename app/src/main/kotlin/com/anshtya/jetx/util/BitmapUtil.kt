package com.anshtya.jetx.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object BitmapUtil {
    fun Bitmap.getByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close()
        return byteArray
    }
}