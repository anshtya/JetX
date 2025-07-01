package com.anshtya.jetx.util

import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileUtil {
    fun getAttachmentCacheDirectory(context: Context): File {
        val dir = File(context.cacheDir, "attachments")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getAttachmentStorageDirectory(context: Context): File {
        val dir = File(context.filesDir, "attachments")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun createFile(
        filePath: File,
        name: String = generateName(),
        ext: String
    ): File = File(filePath, "${name}.${ext}")

    private fun generateName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")
        return LocalDateTime.now().format(formatter)
    }
}