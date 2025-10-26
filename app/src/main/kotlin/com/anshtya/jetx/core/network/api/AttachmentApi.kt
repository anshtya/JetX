package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.NetworkAttachment
import com.anshtya.jetx.core.network.model.response.CreateAttachmentResponse
import com.anshtya.jetx.core.network.model.response.FileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface AttachmentApi {
    @GET("attachment/upload")
    suspend fun getAttachmentUploadUrl(
        @Query("name") name: String,
        @Query("contentType") contentType: String
    ): Response<FileResponse>

    @GET("attachment/{id}")
    suspend fun getAttachment(
        @Path("id") id: UUID
    ): Response<NetworkAttachment>

    @POST("attachment/create")
    suspend fun createAttachment(
        @Body body: NetworkAttachment
    ): Response<CreateAttachmentResponse>
}