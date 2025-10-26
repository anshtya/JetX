package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.StorageApi
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.FileResponse
import com.anshtya.jetx.core.network.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor(
    private val storageApi: StorageApi
) {
    suspend fun getFileUploadUrl(
        name: String,
        contentType: String
    ): NetworkResult<FileResponse> {
        return safeApiCall {
            storageApi.getUploadUrl(
                name = name,
                contentType = contentType
            )
        }
    }

    suspend fun getFileDownloadUrl(
        name: String
    ): NetworkResult<FileResponse> {
        return safeApiCall {
            storageApi.getDownloadUrl(name)
        }
    }

    suspend fun deleteFileFromStorage(
        name: String
    ): NetworkResult<Unit> {
        return safeApiCall {
            storageApi.deleteFromStorage(name)
        }
    }
}