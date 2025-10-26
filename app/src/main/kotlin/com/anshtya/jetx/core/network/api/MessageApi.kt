package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.NetworkMessage
import com.anshtya.jetx.core.network.model.body.MessageUpdateBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface MessageApi {
    @GET("message/{id}")
    suspend fun getMessage(
        @Path("id") id: UUID
    ): Response<NetworkMessage>

    @POST("message/create")
    suspend fun sendMessage(
        @Body body: NetworkMessage
    ): Response<Unit>

    @PATCH("message/received/all")
    suspend fun markMessagesReceived(
        @Body dto: MessageUpdateBody
    ): Response<Unit>

    @PATCH("message/received/{id}")
    suspend fun markMessageReceived(
        @Path("id") id: UUID
    ): Response<Unit>

    @PATCH("message/seen/all")
    suspend fun markMessagesSeen(
        @Body dto: MessageUpdateBody
    ): Response<Unit>
}