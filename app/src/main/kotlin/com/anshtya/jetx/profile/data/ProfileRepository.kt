package com.anshtya.jetx.profile.data

import android.graphics.Bitmap
import com.anshtya.jetx.core.model.UserProfile
import java.util.UUID

interface ProfileRepository {
    suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit>

    suspend fun saveProfile(userId: String): Boolean

    suspend fun getProfile(userId: UUID): UserProfile?

    suspend fun searchProfiles(query: String): Result<List<UserProfile>>

    suspend fun deleteProfiles()
}