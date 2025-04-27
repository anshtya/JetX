package com.anshtya.jetx.attachments

import android.net.Uri

sealed class AttachmentFormat {
    data class UriAttachment(
        val uri: Uri,
        val type: AttachmentType
    ) : AttachmentFormat()
    data class UrlAttachment(
        val url: String,
        val type: AttachmentType
    ) : AttachmentFormat()
    object None : AttachmentFormat()
}