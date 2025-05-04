package com.anshtya.jetx.util

import java.util.Locale
import kotlin.math.roundToInt

fun getReadableFileSize(bytes: Long): String {
    var size = bytes.toFloat()
    var readableFileSize = ""
    listOf("B", "KB", "MB").forEach { unit ->
        println("$size, $unit")
        if (size < 1024) {
            readableFileSize = String.format(Locale.getDefault(), "%d %s", size.roundToInt(), unit)
            return readableFileSize
        }
        size /= 1024
    }
    return readableFileSize
}