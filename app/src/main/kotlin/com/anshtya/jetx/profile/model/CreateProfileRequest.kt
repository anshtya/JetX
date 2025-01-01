package com.anshtya.jetx.profile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileRequest(
    val name: String,
    val username: String,
    @SerialName("picture_url")
    val profilePictureUrl: String?
)
