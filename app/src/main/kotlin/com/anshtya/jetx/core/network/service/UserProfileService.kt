package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.UserProfileApi
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.body.FcmBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
import com.anshtya.jetx.core.network.model.body.NameBody
import com.anshtya.jetx.core.network.model.body.UsernameBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import com.anshtya.jetx.core.network.util.safeApiCall
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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
        profileBody: CreateProfileBody,
        photo: File?
    ): NetworkResult<GetUserProfileResponse> {
        return safeApiCall {
            val profileRequestBody = Json.encodeToString(profileBody)
                .toRequestBody("application/json".toMediaType())
            val dataPart = MultipartBody.Part.createFormData("data", null, profileRequestBody)

            val photoPart = getPartForPhoto(photo)

            userProfileApi.createProfile(
                profile = dataPart,
                photo = photoPart
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

    suspend fun updateProfilePhoto(
        photo: File
    ): NetworkResult<Unit> {
        return safeApiCall {
            val photoPart = getPartForPhoto(photo)
            userProfileApi.updateProfilePhoto(photoPart!!)
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

    private fun getPartForPhoto(
        photo: File?
    ): MultipartBody.Part? {
        val photoRequestBody = photo?.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return photoRequestBody?.let {
            MultipartBody.Part.createFormData("photo", photo.name, body = it)
        }
    }
}