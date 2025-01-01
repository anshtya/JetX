package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anshtya.jetx.database.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId")
    suspend fun getUserProfile(userId: String): UserProfileEntity

    @Upsert
    suspend fun upsertUserProfile(userProfile: UserProfileEntity)

    @Upsert
    suspend fun upsertUserProfiles(userProfiles: List<UserProfileEntity>)

    @Delete
    suspend fun deleteUserProfiles(userProfiles: List<UserProfileEntity>)

    @Query("DELETE FROM user_profile")
    suspend fun deleteAllProfiles()
}