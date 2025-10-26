package com.anshtya.jetx.core.network.model.response

import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserProfileSearchResponse(
    val users: List<UserProfileSearchItem>
)

@Serializable
data class UserProfileSearchItem(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val displayName: String
)