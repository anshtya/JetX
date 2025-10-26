package com.anshtya.jetx.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anshtya.jetx.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getUserProfile(id: UUID): UserProfileEntity

    @Query("SELECT EXISTS(SELECT 1 FROM user_profile WHERE id = :id)")
    suspend fun userProfileExists(id: UUID): Boolean

    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserProfileFlow(id: UUID): Flow<UserProfileEntity?>

    @Upsert
    suspend fun upsertUserProfile(userProfile: UserProfileEntity)

    @Upsert
    suspend fun upsertUserProfiles(userProfiles: List<UserProfileEntity>)

    @Delete
    suspend fun deleteUserProfiles(userProfiles: List<UserProfileEntity>)

    @Query("DELETE FROM user_profile")
    suspend fun deleteAllProfiles()

    @Query("UPDATE user_profile SET name = :name WHERE id = :id")
    suspend fun updateName(
        id: UUID,
        name: String
    )

    @Query("UPDATE user_profile SET username = :username WHERE id = :id")
    suspend fun updateUsername(
        id: UUID,
        username: String
    )

    @Query("UPDATE user_profile SET profile_picture = :profilePicture WHERE id = :id")
    suspend fun updateProfilePicture(
        id: UUID,
        profilePicture: String?
    )
}