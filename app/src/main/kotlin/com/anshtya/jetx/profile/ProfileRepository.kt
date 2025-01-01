package com.anshtya.jetx.profile

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profileStatus: Flow<Boolean>

    suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit>

    suspend fun fetchAndSaveProfile(id: String)

    suspend fun deleteProfiles()
}