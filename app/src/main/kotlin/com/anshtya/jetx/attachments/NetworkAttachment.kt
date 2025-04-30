package com.anshtya.jetx.attachments

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAttachment(
    val url: String,
    val type: AttachmentType,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class AttachmentUploadResponse(
    val id: Int
)
