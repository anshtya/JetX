package com.anshtya.jetx.auth.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    val username: String,
    val password: String
)
