package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.body.CheckUsernameBody
import com.anshtya.jetx.core.network.model.body.GetUserProfileBody
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
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
    ): Response<Unit>

    @POST("user/check_username")
    suspend fun checkUsername(
        @Body body: CheckUsernameBody
    ): Response<CheckUsernameResponse>
}