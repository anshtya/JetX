package com.anshtya.jetx.s3

import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.network.di.qualifiers.Base
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class S3 @Inject constructor(
    @Base private val client: OkHttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun upload(
        url: String,
        byteArray: ByteArray,
        contentType: String
    ): Result<Unit> = runCatching {
        val request = Request.Builder()
            .url(url)
            .put(byteArray.toRequestBody(contentType.toMediaType()))
            .build()

        withContext(ioDispatcher) {
            client.newCall(request).execute()
        }
    }

    suspend fun download(
        url: String
    ): Result<DownloadedFile> = runCatching {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        withContext(ioDispatcher) {
            val response = client.newCall(request).execute()
            DownloadedFile(
                bytes = response.body.byteStream(),
                mimeType = response.header("Content-Type")!!
            )
        }
    }
}