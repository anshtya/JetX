package com.anshtya.jetx.core.network.model.body

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MessageUpdateBody(
    val messageIds: List<@Serializable(with = UUIDSerializer::class) UUID>
)
