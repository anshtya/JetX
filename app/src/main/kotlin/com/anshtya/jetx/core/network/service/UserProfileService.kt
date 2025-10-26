package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.UserProfileApi
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.body.FcmBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
import com.anshtya.jetx.core.network.model.body.NameBody
import com.anshtya.jetx.core.network.model.body.UsernameBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.FileResponse
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import com.anshtya.jetx.core.network.model.response.UserProfileSearchResponse
import com.anshtya.jetx.core.network.util.safeApiCall
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileService @Inject constructor(
    private val userProfileApi: UserProfileApi
) {
    suspend fun getProfileById(
        id: UUID
    ): NetworkResult<GetUserProfileResponse> {
        return safeApiCall {
            userProfileApi.getProfileById(GetUserProfileBody(id))
        }
    }

    suspend fun createProfile(
        displayName: String,
        username: String,
        fcmToken: String,
        photoExists: Boolean
    ): NetworkResult<GetUserProfileResponse> {
        return safeApiCall {
            userProfileApi.createProfile(
                CreateProfileBody(
                    displayName = displayName,
                    username = username,
                    fcmToken = fcmToken,
                    photoExists = photoExists
                )
            )
        }
    }

    suspend fun checkUsername(
        username: String
    ): NetworkResult<CheckUsernameResponse> {
        return safeApiCall {
            userProfileApi.checkUsername(
                UsernameBody(username)
            )
        }
    }

    suspend fun updateName(
        name: String
    ): NetworkResult<Unit> {
        return safeApiCall {
            userProfileApi.updateName(NameBody(name))
        }
    }

    suspend fun getDownloadProfilePhotoUrl(): NetworkResult<FileResponse> {
        return safeApiCall {
            userProfileApi.getDownloadProfilePhotoUrl()
        }
    }

    suspend fun getUploadProfilePhotoUrl(
        contentType: String
    ): NetworkResult<FileResponse> {
        return safeApiCall {
            userProfileApi.getUploadProfilePhotoUrl(contentType)
        }
    }

    suspend fun removeProfilePhoto(): NetworkResult<Unit> {
        return safeApiCall {
            userProfileApi.removeProfilePhoto()
        }
    }

    suspend fun updateUsername(
        username: String
    ): NetworkResult<Unit> {
        return safeApiCall {
            userProfileApi.updateUsername(UsernameBody(username))
        }
    }

    suspend fun updateFcmToken(
        token: String
    ): NetworkResult<Unit> {
        return safeApiCall {
            userProfileApi.updateFcmToken(FcmBody(token))
        }
    }

    suspend fun searchUserProfile(
        query: String
    ): NetworkResult<UserProfileSearchResponse> {
        return safeApiCall {
            userProfileApi.searchUserProfiles(query)
        }
    }
}