package com.anshtya.jetx.core.network.model.body

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GetUserProfileBody(
    @Serializable(UUIDSerializer::class)
    val userId: UUID
)
