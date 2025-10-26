package com.anshtya.jetx.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CheckUsernameResponse(
    val valid: Boolean,
    val message: String?
)
