package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.anshtya.jetx.database.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Upsert
    suspend fun upsertUserProfile(userProfile: UserProfileEntity)

    @Upsert
    suspend fun upsertUserProfiles(userProfiles: List<UserProfileEntity>)

    @Delete
    suspend fun deleteUserProfiles(userProfiles: List<UserProfileEntity>)
}