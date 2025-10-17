package com.anshtya.jetx.s3

import java.io.InputStream

data class DownloadedFile(
    val bytes: InputStream,
    val mimeType: String
)
