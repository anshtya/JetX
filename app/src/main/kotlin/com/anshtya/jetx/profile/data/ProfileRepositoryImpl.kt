package com.anshtya.jetx.profile.data

import android.graphics.Bitmap
import android.util.Log
import com.anshtya.jetx.attachments.ImageCompressor
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.database.entity.UserProfileEntity
import com.anshtya.jetx.core.database.entity.toExternalModel
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.profile.data.model.NetworkProfile
import com.anshtya.jetx.profile.data.model.toEntity
import com.anshtya.jetx.profile.data.model.toExternalModel
import kotlinx.coroutines.flow.first
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileService: UserProfileService,
    private val avatarManager: AvatarManager,
    private val fcmTokenManager: FcmTokenManager,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val imageCompressor: ImageCompressor
) : ProfileRepository {
    private val tag = this::class.simpleName

    override suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit> = runCatching {
        val userId = authRepository.authState.first().currentUserIdOrNull()!!

        val profilePictureFile: File? = if (profilePicture != null) {
            val imageByteArray = imageCompressor.compressImage(profilePicture).getOrThrow()
            avatarManager.saveAvatar(
                userId = userId,
                byteArray = imageByteArray
            ).getOrThrow()
        } else {
            null
        }

        userProfileService.createProfile(
            CreateProfileBody(
                displayName = name,
                username = username,
                fcmToken = fcmTokenManager.getToken()
            ),
            photo = profilePictureFile
        )
            .toResult()
            .onFailure { throwable ->
                Log.e(tag, throwable.message, throwable)
                return Result.failure(throwable)
            }

        userProfileDao.upsertUserProfile(
            UserProfileEntity(
                id = UUID.fromString(userId),
                name = name,
                username = username,
                profilePicture = profilePictureFile?.absolutePath
            )
        )
        preferencesStore.setProfileCreated()
    }

    override suspend fun checkUsername(
        username: String
    ): Result<CheckUsernameResponse> {
        return userProfileService.checkUsername(username).toResult()
    }

    override suspend fun saveProfile(userId: String): Boolean {
        val networkProfile = fetchProfile(userId)
        return networkProfile?.let {
            userProfileDao.upsertUserProfile(it.toEntity())
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
                userProfileDao.upsertUserProfile(it.toEntity())
                return@let it.toExternalModel()
            }
        }
    }

    override suspend fun searchProfiles(query: String): Result<List<UserProfile>> {
        return runCatching {
            emptyList() // TODO: implement
        }
    }

    override suspend fun deleteProfiles() {
        userProfileDao.deleteAllProfiles()
        preferencesStore.clearPreferences()
    }

    private suspend fun fetchProfile(userId: String): NetworkProfile? {
        return null // TODO: implement
    }
}