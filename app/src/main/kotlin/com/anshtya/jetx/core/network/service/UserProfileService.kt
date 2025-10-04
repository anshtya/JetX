package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.UserProfileApi
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.body.CheckUsernameBody
import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileService @Inject constructor(
    private val userProfileApi: UserProfileApi
) {
    suspend fun getProfileById(
        id: String
    ): NetworkResult<GetUserProfileResponse> {
        return safeApiCall {
            userProfileApi.getProfileById(
                GetUserProfileBody(id)
            )
        }
    }

    suspend fun createProfile(
        profileBody: CreateProfileBody,
        photo: File?
    ): NetworkResult<Unit> {
        return safeApiCall {
            val profileRequestBody = Json.encodeToString(profileBody)
                .toRequestBody("application/json".toMediaType())
            val dataPart = MultipartBody.Part.createFormData("data", null, profileRequestBody)

            val photoRequestBody = photo?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val photoPart = photoRequestBody?.let {
                MultipartBody.Part.createFormData("photo", photo.name, body = it)
            }

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
                CheckUsernameBody(username)
            )
        }
    }
}