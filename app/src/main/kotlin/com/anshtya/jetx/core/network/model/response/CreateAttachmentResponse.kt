package com.anshtya.jetx.core.network.model.response

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateAttachmentResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID
)
