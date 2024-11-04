package com.anshtya.jetx.data.network.service

import com.anshtya.jetx.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MainService {
    @GET("user/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserResponse
}