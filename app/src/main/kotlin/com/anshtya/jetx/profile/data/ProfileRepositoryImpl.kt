package com.anshtya.jetx.profile.data

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.anshtya.jetx.attachments.ImageCompressor
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.database.entity.UserProfileEntity
import com.anshtya.jetx.core.database.entity.toExternalModel
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.UserProfileSearchItem
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.profile.util.toEntity
import com.anshtya.jetx.s3.S3
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val s3: S3,
    private val attachmentRepository: AttachmentRepository,
    private val authManager: AuthManager,
    private val avatarManager: AvatarManager,
    private val fcmTokenManager: FcmTokenManager,
    private val store: JetxPreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val imageCompressor: ImageCompressor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {
    private val tag = this::class.simpleName

    override suspend fun createProfile(
        name: String,
        username: String,
        photo: Uri?
    ): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!
        val profilePhotoPath = photo?.let {
            uploadProfilePhoto(photo, userId).getOrThrow()
        }

        val fcmToken = fcmTokenManager.getToken()
        val networkUserProfile = userProfileService.createProfile(
            displayName = name,
            username = username,
            fcmToken = fcmToken,
            photoExists = profilePhotoPath != null
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
                profilePicture = profilePhotoPath
            )
        )

        store.account.storeFcmToken(fcmToken)
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
        val userProfileExists = userProfileDao.userProfileExists(userId)
        if (userProfileExists) return Result.success(Unit)

        val userProfile = userProfileService.getProfileById(userId)
            .toResult()
            .getOrThrow()

        val profilePhoto: String? = if (userProfile.photoExists) {
            val downloadUrl = userProfileService.getDownloadProfilePhotoUrl()
                .toResult()
                .getOrElse {
                    Log.e(tag, it.message, it)
                    return Result.failure(it)
                }
                .url
            val downloadedFile = s3.download(downloadUrl)
                .getOrElse {
                    Log.e(tag, "Failed to download avatar of user id: $userId", it)
                    return Result.failure(it)
                }

            avatarManager.saveAvatar(
                userId = userId.toString(),
                byteArray = withContext(ioDispatcher) {
                    downloadedFile.bytes.use { it.readBytes() }
                },
                ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(downloadedFile.mimeType)!!
            ).getOrElse {
                return Result.failure(it)
            }.absolutePath
        } else {
            null
        }

        userProfileDao.upsertUserProfile(
            userProfile.toEntity(userId, profilePhoto = profilePhoto)
        )
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

    override suspend fun searchProfiles(
        query: String
    ): Result<List<UserProfileSearchItem>> {
        return runCatching {
            userProfileService.searchUserProfile(query).toResult().getOrThrow().users
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
        photo: Uri
    ): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!
        val photoPath = uploadProfilePhoto(photo, userId).getOrThrow()
        userProfileDao.updateProfilePicture(
            id = userId,
            profilePicture = photoPath
        )
    }

    override suspend fun removeProfilePhoto(): Result<Unit> = runCatching {
        val userId = authManager.authState.value.currentUserIdOrNull()!!
        val photoPath = userProfileDao.getUserProfile(userId).profilePicture
            ?: return Result.success(Unit)

        userProfileService.removeProfilePhoto()
            .toResult()
            .onFailure {
                Log.e(tag, "Failed to remove avatar", it)
                return Result.failure(it)
            }

        avatarManager.deleteAvatar(photoPath)

        userProfileDao.updateProfilePicture(
            id = userId,
            profilePicture = null
        )
    }

    private suspend fun uploadProfilePhoto(
        photo: Uri,
        userId: UUID
    ): Result<String> {
        val mimeType = attachmentRepository.getMimeType(photo)
            ?: return Result.failure(Exception("Invalid photo type"))

        val photoByteArray = imageCompressor.compressImage(
            uri = photo,
            mimeType = mimeType
        ).getOrElse {
            Log.e(tag, "Failed to compress avatar", it)
            return Result.failure(it)
        }

        val uploadUrl = userProfileService.getUploadProfilePhotoUrl(
            contentType = mimeType
        )
            .toResult()
            .getOrElse {
                Log.e(tag, "Failed to generate avatar upload url", it)
                return Result.failure(it)
            }.url
        s3.upload(
            url = uploadUrl,
            byteArray = photoByteArray,
            contentType = mimeType,
        ).getOrElse {
            Log.e(tag, "Failed to upload avatar", it)
            return Result.failure(it)
        }

        val path = avatarManager.saveAvatar(
            userId = userId.toString(),
            byteArray = photoByteArray,
            ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)!!
        ).getOrElse {
            return Result.failure(it)
        }.absolutePath

        return Result.success(path)
    }
}