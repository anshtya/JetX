package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.response.FileResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StorageApi {
    @GET("storage/upload")
    suspend fun getUploadUrl(
        @Query("name") name: String,
        @Query("contentType") contentType: String
    ): Response<FileResponse>

    @GET("storage/{fileName}")
    suspend fun getDownloadUrl(
        @Path("fileName") name: String
    ): Response<FileResponse>

    @DELETE("storage/{fileName}")
    suspend fun deleteFromStorage(
        @Path("fileName") name: String
    ): Response<Unit>
}