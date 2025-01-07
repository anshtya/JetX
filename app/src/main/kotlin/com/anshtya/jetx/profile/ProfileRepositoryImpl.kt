package com.anshtya.jetx.profile

import android.graphics.Bitmap
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.ProfileValues
import com.anshtya.jetx.profile.model.CreateProfileRequest
import com.anshtya.jetx.profile.model.NetworkProfile
import com.anshtya.jetx.profile.model.toEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao
) : ProfileRepository {
    private val supabaseStorage = client.storage
    private val supabasePostgrest = client.postgrest
    private val supabaseAuth = client.auth

    private companion object {
        const val PROFILE_TABLE = "profile"
        const val MEDIA_STORAGE = "media"
    }

    override val profileStatus: Flow<Boolean> = preferencesStore
        .getBooleanFlow(ProfileValues.PROFILE_CREATED)
        .map { it ?: false }

    override suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit> {
        return kotlin.runCatching {
            val userId = supabaseAuth.retrieveUserForCurrentSession().id
            var profilePicturePath: String? = null

            if (profilePicture != null) {
                val imageByteArray = getByteArrayFromBitmap(profilePicture)
                val mediaBucket = supabaseStorage.from(MEDIA_STORAGE)
                val path = "${userId}/profile-${username}.png"
                mediaBucket.upload(
                    path = path,
                    data = imageByteArray
                )
                profilePicturePath = mediaBucket.publicUrl(path)
            }

            supabasePostgrest.from(PROFILE_TABLE).insert(
                value = CreateProfileRequest(
                    name = name,
                    username = username,
                    profilePictureUrl = profilePicturePath
                )
            )
            fetchAndSaveProfile(userId)
        }
    }

    override suspend fun fetchAndSaveProfile(id: String) {
        val userProfile = supabasePostgrest.from(PROFILE_TABLE).select {
            filter { eq(column = "user_id", value = id) }
        }.decodeSingleOrNull<NetworkProfile>()

        if (userProfile != null) {
            saveProfile(userProfile.toEntity())
        } else {
            throw IllegalStateException("Profile doesn't exist")
        }
    }

    override suspend fun deleteProfiles() {
        userProfileDao.deleteAllProfiles()
        preferencesStore.clearPreferences()
    }

    private fun getByteArrayFromBitmap(imageBitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close()
        return byteArray
    }

    private suspend fun saveProfile(userProfileEntity: UserProfileEntity) {
        userProfileDao.upsertUserProfile(userProfileEntity)

        // If there's no user logged in, then update preferences
        val currentUserId = preferencesStore.getString(ProfileValues.USER_ID)
        if (currentUserId == null) {
            preferencesStore.setString(ProfileValues.USER_ID, userProfileEntity.id.toString())
            preferencesStore.setBoolean(ProfileValues.PROFILE_CREATED, true)
        }
    }
}