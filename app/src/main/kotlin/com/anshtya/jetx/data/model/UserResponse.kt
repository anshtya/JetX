package com.anshtya.jetx.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    val user: String
)
