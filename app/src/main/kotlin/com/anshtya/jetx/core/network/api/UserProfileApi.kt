package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.body.CreateProfileBody
import com.anshtya.jetx.core.network.model.body.FcmBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
import com.anshtya.jetx.core.network.model.body.NameBody
import com.anshtya.jetx.core.network.model.body.UsernameBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.FileResponse
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserProfileApi {
    @POST("user/get")
    suspend fun getProfileById(
        @Body body: GetUserProfileBody
    ): Response<GetUserProfileResponse>

    @POST("user/create")
    suspend fun createProfile(
        @Body body: CreateProfileBody
    ): Response<GetUserProfileResponse>

    @POST("user/check_username")
    suspend fun checkUsername(
        @Body body: UsernameBody
    ): Response<CheckUsernameResponse>

    @PATCH("user/name/update")
    suspend fun updateName(
        @Body body: NameBody
    ): Response<Unit>

    @GET("user/photo/download")
    suspend fun getDownloadProfilePhotoUrl(): Response<FileResponse>

    @GET("user/photo/upload")
    suspend fun getUploadProfilePhotoUrl(
        @Query("contentType") contentType: String
    ): Response<FileResponse>

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