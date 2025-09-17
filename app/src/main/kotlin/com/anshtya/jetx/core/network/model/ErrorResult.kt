package com.anshtya.jetx.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResult(
    val status: Int,
    val message: String,
)
