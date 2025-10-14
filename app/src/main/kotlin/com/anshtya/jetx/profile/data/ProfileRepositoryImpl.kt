package com.anshtya.jetx.profile.data

import android.graphics.Bitmap
import android.util.Log
import com.anshtya.jetx.attachments.ImageCompressor
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.database.entity.UserProfileEntity
import com.anshtya.jetx.core.database.entity.toExternalModel
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.profile.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val authManager: AuthManager,
    private val avatarManager: AvatarManager,
    private val fcmTokenManager: FcmTokenManager,
    private val store: JetxPreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val imageCompressor: ImageCompressor
) : ProfileRepository {
    private val tag = this::class.simpleName

    override suspend fun createProfile(
        name: String,
        username: String,
        profilePicture: Bitmap?
    ): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!

        val profilePictureFile: File? = if (profilePicture != null) {
            val imageByteArray = imageCompressor.compressImage(profilePicture).getOrElse {
                Log.e(tag, "Failed to compress avatar of user id: $userId", it)
                return Result.failure(it)
            }
            avatarManager.saveAvatar(
                userId = userId.toString(),
                byteArray = imageByteArray
            ).getOrElse {
                Log.e(tag, "Failed to save avatar of user id: $userId", it)
                return Result.failure(it)
            }
        } else {
            null
        }

        val networkUserProfile = userProfileService.createProfile(
            CreateProfileBody(
                displayName = name,
                username = username,
                fcmToken = fcmTokenManager.getToken()
            ),
            photo = profilePictureFile
        )
            .toResult()
            .getOrElse { throwable ->
                Log.e(tag, throwable.message, throwable)
                return Result.failure(throwable)
            }

        userProfileDao.upsertUserProfile(
            UserProfileEntity(
                id = userId,
                name = name,
                username = username,
                phoneNumber = networkUserProfile.phoneNumber,
                profilePicture = profilePictureFile?.absolutePath
            )
        )

        store.user.setProfileCreated()
    }

    override suspend fun checkUsername(
        username: String
    ): Result<CheckUsernameResponse> {
        return userProfileService.checkUsername(username).toResult()
    }

    override suspend fun fetchAndSaveProfile(
        userId: UUID
    ): Result<Unit> = runCatching {
        val userProfile = userProfileService.getProfileById(userId)
            .toResult()
            .getOrThrow()
        userProfileDao.upsertUserProfile(userProfile.toEntity(userId))
    }

    override suspend fun getProfile(
        userId: UUID
    ): UserProfile {
        return userProfileDao.getUserProfile(userId).toExternalModel()
    }

    override fun getProfileFlow(
        userId: UUID
    ): Flow<UserProfile?> {
        return userProfileDao.getUserProfileFlow(userId).map { it?.toExternalModel() }
    }

    override suspend fun searchProfiles(query: String): Result<List<UserProfile>> {
        return runCatching {
            emptyList() // TODO: implement
        }
    }

    override suspend fun updateName(
        name: String
    ): Result<Unit> = runCatching {
        userProfileService.updateName(name)
            .toResult()
            .onFailure {
                Log.e(tag, it.message, it)
                return Result.failure(it)
            }

        val userId = authManager.authState.value.currentUserIdOrNull()!!
        userProfileDao.updateName(
            id = userId,
            name = name
        )
    }

    override suspend fun updateUsername(
        username: String
    ): Result<Unit> = runCatching {
        userProfileService.updateUsername(username)
            .toResult()
            .onFailure {
                Log.e(tag, it.message, it)
                return Result.failure(it)
            }

        val userId = authManager.authState.value.currentUserIdOrNull()!!
        userProfileDao.updateUsername(
            id = userId,
            username = username
        )
    }

    override suspend fun updateProfilePhoto(
        profilePhoto: Bitmap
    ): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!

        val photoByteArray = imageCompressor.compressImage(profilePhoto).getOrElse {
            Log.e(tag, "Failed to compress avatar of user id: $userId", it)
            return Result.failure(it)
        }
        val photoFile = avatarManager.saveTempAvatar(
            userId = userId.toString(),
            byteArray = photoByteArray
        ).getOrElse {
            Log.e(tag, "Failed to save temp avatar of user id: $userId", it)
            return Result.failure(it)
        }

        userProfileService.updateProfilePhoto(photo = photoFile)
            .toResult()
            .getOrElse {
                Log.e(tag, "Failed to upload avatar of user id: $userId", it)
                return Result.failure(it)
            }

        val file = avatarManager.updateAvatar(
            userId = userId.toString(),
            newByteArray = photoByteArray
        ).getOrElse {
            Log.e(tag, "Failed to save avatar of user id: $userId", it)
            return Result.failure(it)
        }
        userProfileDao.updateProfilePicture(
            id = userId,
            profilePicture = file.absolutePath
        )
    }

    override suspend fun removeProfilePhoto(): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!

        userProfileService.removeProfilePhoto()
            .toResult()
            .onFailure {
                Log.e(tag, "Failed to remove avatar of user id: $userId", it)
                return Result.failure(it)
            }
        avatarManager.deleteAvatar(userId.toString()).getOrElse {
            Log.e(tag, "Failed to delete avatar of user id: $userId", it)
        }
        userProfileDao.updateProfilePicture(
            id = userId,
            profilePicture = null
        )
    }
}