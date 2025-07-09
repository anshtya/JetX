package com.anshtya.jetx.shared.attachments

sealed interface Attachment {
    data class UriAttachment(
        val absolutePath: String,
        val attachmentMetadata: AttachmentMetadata
    ) : Attachment

    data class UrlAttachment(val networkAttachment: NetworkAttachment) : Attachment
    object None : Attachment
}