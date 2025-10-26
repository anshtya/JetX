package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.AttachmentApi
import com.anshtya.jetx.core.network.model.NetworkAttachment
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.CreateAttachmentResponse
import com.anshtya.jetx.core.network.model.response.FileResponse
import com.anshtya.jetx.core.network.util.safeApiCall
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentService @Inject constructor(
    private val attachmentApi: AttachmentApi
) {
    suspend fun getAttachmentUploadUrl(
        name: String,
        contentType: String
    ): NetworkResult<FileResponse> {
        return safeApiCall {
            attachmentApi.getAttachmentUploadUrl(
                name = name,
                contentType = contentType
            )
        }
    }

    suspend fun getAttachment(
        id: UUID
    ): NetworkResult<NetworkAttachment> {
        return safeApiCall {
            attachmentApi.getAttachment(id)
        }
    }

    suspend fun createAttachment(
        name: String,
        type: String,
        width: Int,
        height: Int
    ): NetworkResult<CreateAttachmentResponse> {
        return safeApiCall {
            attachmentApi.createAttachment(
                NetworkAttachment(
                    name = name,
                    type = type,
                    width = width,
                    height = height
                )
            )
        }
    }
}