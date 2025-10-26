package com.anshtya.jetx.attachments.data

import android.net.Uri
import com.anshtya.jetx.core.network.model.NetworkAttachment

sealed class AttachmentFormat {
    data class UriAttachment(
        val uri: Uri,
        val attachmentMetadata: AttachmentMetadata
    ) : AttachmentFormat()

    data class ServerAttachment(
        val networkAttachment: NetworkAttachment
    ) : AttachmentFormat()

    object None : AttachmentFormat()
}