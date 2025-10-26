package com.anshtya.jetx.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAttachment(
    val name: String,
    val type: String,
    val width: Int,
    val height: Int
)