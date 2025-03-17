package com.anshtya.jetx.profile

import android.graphics.Bitmap
import com.anshtya.jetx.common.model.UserProfile
import java.util.UUID

interface ProfileRepository {
    suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit>

    suspend fun saveProfile(userId: String)

    suspend fun getProfile(userId: UUID): UserProfile?

    suspend fun searchProfiles(query: String): List<UserProfile>

    suspend fun deleteProfiles()
}