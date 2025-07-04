package com.anshtya.jetx.attachments.data

enum class AttachmentType {
    IMAGE,
    VIDEO;

    companion object {
        fun fromMimeType(mimeType: String): AttachmentType? {
            return when {
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO
                else -> null
            }
        }
    }
}