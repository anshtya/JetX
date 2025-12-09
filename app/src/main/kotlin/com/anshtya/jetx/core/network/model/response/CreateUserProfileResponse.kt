package com.anshtya.jetx.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserProfileResponse(
    val username: String,
    val displayName: String,
    val phoneNumber: String
)