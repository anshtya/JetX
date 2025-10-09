package com.anshtya.jetx.profile.util

import com.anshtya.jetx.core.database.entity.UserProfileEntity
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import java.util.UUID

fun GetUserProfileResponse.toEntity(
    userId: UUID
): UserProfileEntity {
    return UserProfileEntity(
        id = userId,
        name = displayName,
        username = username,
        phoneNumber = phoneNumber,
        profilePicture = null // TODO: manage profile picture at server
    )
}