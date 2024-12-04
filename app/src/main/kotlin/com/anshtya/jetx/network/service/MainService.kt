package com.anshtya.jetx.network.service

import com.anshtya.jetx.network.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MainService {
    @GET("user/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserResponse
}