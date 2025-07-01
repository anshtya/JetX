package com.anshtya.jetx.attachments.data

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAttachment(
    val url: String,
    val type: AttachmentType,
    val width: Int? = null,
    val height: Int? = null,
    val size: String
)

@Serializable
data class AttachmentUploadResponse(
    val id: Int
)
