package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "user_profile"
)
data class UserProfileEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val username: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String?
)