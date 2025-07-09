package com.anshtya.jetx.shared.attachments

expect class ImageCompressor {
    suspend fun compressImage(
        imagePath: String
    ): Result<ByteArray>

    suspend fun compressImage(
        image: Image
    ): Result<ByteArray>
}