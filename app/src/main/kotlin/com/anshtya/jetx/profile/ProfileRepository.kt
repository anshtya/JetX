package com.anshtya.jetx.profile

import com.anshtya.jetx.profile.model.Profile
import com.anshtya.jetx.profile.model.ProfileStatus
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profileStatus: Flow<ProfileStatus>

    suspend fun createProfile(profile: Profile): Result<Unit>

    suspend fun getProfile(id: String): Result<Profile>
}