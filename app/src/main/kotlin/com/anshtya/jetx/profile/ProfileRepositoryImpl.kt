package com.anshtya.jetx.profile

import android.graphics.Bitmap
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.ProfileValues
import com.anshtya.jetx.profile.model.CreateProfileRequest
import com.anshtya.jetx.profile.model.NetworkProfile
import com.anshtya.jetx.profile.model.ProfileStatus
import com.anshtya.jetx.profile.model.toEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    override val profileStatus: Flow<ProfileStatus> = preferencesStore
        .getBooleanFlow(AuthValues.PROFILE_CREATED)
        .distinctUntilChanged()
        .map { status ->
            status?.let { created ->
                if (created) ProfileStatus.CREATED else ProfileStatus.NOT_CREATED
            } ?: ProfileStatus.NOT_CREATED
        }
    private companion object {
        const val PROFILE_TABLE = "profile"
        const val MEDIA_STORAGE = "media"
    }

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
        preferencesStore.setString(ProfileValues.USER_ID, "")
        preferencesStore.setBoolean(ProfileValues.PROFILE_CREATED, false)
    }

    private fun getByteArrayFromBitmap(imageBitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private suspend fun saveProfile(userProfileEntity: UserProfileEntity) {
        userProfileDao.upsertUserProfile(userProfileEntity)
        preferencesStore.setString(ProfileValues.USER_ID, userProfileEntity.id)
        preferencesStore.setBoolean(ProfileValues.PROFILE_CREATED, true)
    }
}