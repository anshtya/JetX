package com.anshtya.jetx.common.model

import java.util.UUID

data class UserProfile(
    val id: UUID,
    val name: String,
    val username: String,
    val pictureUrl: String?
)
