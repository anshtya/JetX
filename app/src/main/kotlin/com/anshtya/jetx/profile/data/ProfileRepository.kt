package com.anshtya.jetx.profile.data

import android.graphics.Bitmap
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import java.util.UUID

interface ProfileRepository {
    suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit>

    suspend fun checkUsername(username: String): Result<CheckUsernameResponse>

    suspend fun saveProfile(userId: String): Boolean

    suspend fun getProfile(userId: UUID): UserProfile?

    suspend fun searchProfiles(query: String): Result<List<UserProfile>>

    suspend fun deleteProfiles()
}