package com.anshtya.jetx.shared.profile

import com.anshtya.jetx.shared.model.UserProfile
import java.util.UUID

interface ProfileRepository {
    suspend fun createProfile(
        name: String,
        username: String,
        profilePicturePath: String?
    ): Result<Unit>

    suspend fun saveProfile(userId: String): Boolean

    suspend fun getProfile(userId: UUID): UserProfile?

    suspend fun searchProfiles(query: String): Result<List<UserProfile>>

    suspend fun deleteProfiles()
}