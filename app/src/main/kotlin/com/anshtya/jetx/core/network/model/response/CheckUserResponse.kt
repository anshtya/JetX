package com.anshtya.jetx.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CheckUserResponse(
    val exists: Boolean
)
