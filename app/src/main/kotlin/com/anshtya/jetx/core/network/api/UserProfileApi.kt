package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.body.FcmBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
import com.anshtya.jetx.core.network.model.body.NameBody
import com.anshtya.jetx.core.network.model.body.UsernameBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface UserProfileApi {
    @POST("user/get")
    suspend fun getProfileById(
        @Body body: GetUserProfileBody
    ): Response<GetUserProfileResponse>

    @Multipart
    @POST("user/create")
    suspend fun createProfile(
        @Part profile: MultipartBody.Part,
        @Part photo: MultipartBody.Part?
    ): Response<GetUserProfileResponse>

    @POST("user/check_username")
    suspend fun checkUsername(
        @Body body: UsernameBody
    ): Response<CheckUsernameResponse>

    @PATCH("user/name/update")
    suspend fun updateName(
        @Body body: NameBody
    ): Response<Unit>

    @Multipart
    @PATCH("user/photo/update")
    suspend fun updateProfilePhoto(
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    @PATCH("user/photo/remove")
    suspend fun removeProfilePhoto(): Response<Unit>

    @PATCH("user/username/update")
    suspend fun updateUsername(
        @Body body: UsernameBody
    ): Response<Unit>

    @PATCH("user/fcm/update")
    suspend fun updateFcmToken(
        @Body body: FcmBody
    ): Response<Unit>
}