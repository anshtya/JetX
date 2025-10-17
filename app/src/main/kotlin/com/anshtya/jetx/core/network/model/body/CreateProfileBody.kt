package com.anshtya.jetx.core.network.model.body

import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileBody(
    val displayName: String,
    val username: String,
    val fcmToken: String,
    val photoExists: Boolean
)
