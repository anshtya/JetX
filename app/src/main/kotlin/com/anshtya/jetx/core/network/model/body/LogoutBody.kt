package com.anshtya.jetx.core.network.model.body

import kotlinx.serialization.Serializable

@Serializable
data class LogoutBody(
    val token: String
)