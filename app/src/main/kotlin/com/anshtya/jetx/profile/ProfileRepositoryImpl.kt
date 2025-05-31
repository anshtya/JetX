package com.anshtya.jetx.profile

import android.graphics.Bitmap
import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.database.entity.toExternalModel
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.profile.model.CreateProfileRequest
import com.anshtya.jetx.profile.model.NetworkProfile
import com.anshtya.jetx.profile.model.toEntity
import com.anshtya.jetx.profile.model.toExternalModel
import com.anshtya.jetx.util.BitmapUtil.getByteArray
import com.anshtya.jetx.util.Constants.MEDIA_STORAGE
import com.anshtya.jetx.util.Constants.PROFILE_TABLE
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val fcmTokenManager: FcmTokenManager,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao
) : ProfileRepository {
    private val supabaseStorage = client.storage
    private val supabaseAuth = client.auth

    private val profileTable = client.from(PROFILE_TABLE)
    private val mediaBucket = supabaseStorage.from(MEDIA_STORAGE)

    override suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit> {
        return runCatching {
            val userId = supabaseAuth.currentUserOrNull()?.id
                ?: throw IllegalStateException("User should be logged in to create profile")
            var profilePicturePath: String? = null

            if (profilePicture != null) {
                val imageByteArray = profilePicture.getByteArray()
                val path = "profile-${userId}.png"
                mediaBucket.upload(
                    path = path,
                    data = imageByteArray
                )
                profilePicturePath = mediaBucket.publicUrl(path)
            }

            profileTable.insert(
                value = CreateProfileRequest(
                    name = name,
                    username = username,
                    profilePictureUrl = profilePicturePath
                )
            )
            saveProfile(
                UserProfileEntity(
                    id = UUID.fromString(userId),
                    name = name,
                    username = username,
                    profilePicture = profilePicturePath
                )
            )
            fcmTokenManager.addToken()
            preferencesStore.setProfileCreated(true)
        }
    }

    override suspend fun saveProfile(userId: String): Boolean {
        val networkProfile = fetchProfile(userId)
        return networkProfile?.let {
            saveProfile(it.toEntity())
            true
        } ?: false
    }

    override suspend fun getProfile(userId: UUID): UserProfile? {
        val userProfile = userProfileDao.getUserProfile(userId)
        return if (userProfile != null) {
            userProfile.toExternalModel()
        } else {
            val networkProfile = fetchProfile(userId.toString())

            networkProfile?.let {
                saveProfile(it.toEntity())
                return@let it.toExternalModel()
            }
        }
    }

    override suspend fun searchProfiles(query: String): Result<List<UserProfile>> {
        return runCatching {
            val pattern = "%${query}%"
            profileTable.select {
                filter {
                    or {
                        ilike(column = "username", pattern = pattern)
                        ilike(column = "name", pattern = pattern)
                    }
                    and {
                        neq(
                            column = "user_id",
                            value = supabaseAuth.currentUserOrNull()?.id!!
                        )
                    }
                }
            }
                .decodeList<NetworkProfile>()
                .map(NetworkProfile::toExternalModel)
        }
    }

    override suspend fun deleteProfiles() {
        userProfileDao.deleteAllProfiles()
        preferencesStore.clearPreferences()
    }

    private suspend fun fetchProfile(userId: String): NetworkProfile? {
        return profileTable.select {
            filter { eq(column = "user_id", value = userId) }
        }.decodeSingleOrNull<NetworkProfile>()
    }

    private suspend fun saveProfile(userProfileEntity: UserProfileEntity) {
        userProfileDao.upsertUserProfile(userProfileEntity)
    }
}