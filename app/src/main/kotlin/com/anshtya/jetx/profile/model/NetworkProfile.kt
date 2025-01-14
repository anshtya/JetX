package com.anshtya.jetx.profile.model

import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkProfile(
    @SerialName("user_id")
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    val name: String,
    val username: String,
    @SerialName("picture_url")
    val pictureUrl: String?
)

fun NetworkProfile.toEntity() = UserProfileEntity(
    id = userId,
    name = name,
    username = username,
    profilePicture = pictureUrl
)

fun NetworkProfile.toExternalModel() = UserProfile(
    id = userId,
    name = name,
    username = username,
    pictureUrl = pictureUrl
)
