package com.anshtya.jetx.core.network.model.response

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AuthTokenResponse(
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    val accessToken: String,
    val refreshToken: String
)