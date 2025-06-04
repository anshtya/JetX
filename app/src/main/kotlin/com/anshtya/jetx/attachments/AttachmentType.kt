package com.anshtya.jetx.attachments

enum class AttachmentType {
    IMAGE,
    VIDEO,
    DOCUMENT;

    companion object {
        fun fromMimeType(mimeType: String): AttachmentType {
            return when {
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO

                // TODO: restrict types
                else -> DOCUMENT
            }
        }
    }
}