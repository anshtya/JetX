package com.anshtya.jetx.attachments

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAttachment(
    val id: Int,
    val url: String,
    val type: AttachmentType
)

@Serializable
data class NetworkAttachmentUpload(
    val url: String,
    val type: AttachmentType
)

@Serializable
data class NetworkAttachmentUploadResponse(
    val id: Int
)
