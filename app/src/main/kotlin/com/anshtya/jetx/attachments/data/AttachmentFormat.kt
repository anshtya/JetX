package com.anshtya.jetx.attachments.data

import android.net.Uri

sealed class AttachmentFormat {
    data class UriAttachment(
        val uri: Uri,
        val attachmentMetadata: AttachmentMetadata
    ) : AttachmentFormat()

    data class UrlAttachment(val networkAttachment: NetworkAttachment) : AttachmentFormat()
    object None : AttachmentFormat()
}