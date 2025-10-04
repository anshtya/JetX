package com.anshtya.jetx.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
class GetUserProfileResponse(
    val username: String,
    val displayName: String,
    val profilePhotoUrl: String?,
)