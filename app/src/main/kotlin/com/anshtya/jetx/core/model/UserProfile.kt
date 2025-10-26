package com.anshtya.jetx.core.model

import java.util.UUID

data class UserProfile(
    val id: UUID,
    val name: String,
    val username: String,
    val phoneNumber: String,
    val pictureUrl: String?
)
