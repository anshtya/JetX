package com.anshtya.jetx.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anshtya.jetx.core.model.UserProfile
import java.util.UUID

@Entity(
    tableName = "user_profile"
)
data class UserProfileEntity(
    @PrimaryKey
    val id: UUID,

    val name: String,
    val username: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "profile_picture")
    val profilePicture: String?
)

fun UserProfileEntity.toExternalModel(): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        username = username,
        phoneNumber = phoneNumber,
        pictureUrl = profilePicture
    )
}