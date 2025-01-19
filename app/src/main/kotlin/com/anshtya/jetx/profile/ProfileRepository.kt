package com.anshtya.jetx.profile

import android.graphics.Bitmap
import com.anshtya.jetx.common.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileRepository {
    val profileStatus: Flow<Boolean>

    suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit>

    suspend fun fetchAndSaveProfile(id: String)

    suspend fun getProfile(id: UUID): UserProfile?

    suspend fun profileExists(id: UUID): Boolean

    suspend fun searchProfiles(query: String): List<UserProfile>

    suspend fun deleteProfiles()
}