package com.anshtya.jetx.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProfile(
    val name: String,
    val username: String,
    @SerialName("picture_url") val profilePictureUrl: String?
)
