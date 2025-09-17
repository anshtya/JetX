package com.anshtya.jetx.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String
)