package com.anshtya.jetx.core.network.model.body

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentialsBody(
    val phoneNumber: String,
    val pin: String,
    val fcmToken: String? = null
)
