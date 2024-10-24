package com.anshtya.jetx.data.network.model.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    val username: String,
    val password: String
)
