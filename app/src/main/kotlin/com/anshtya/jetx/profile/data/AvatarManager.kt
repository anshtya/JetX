package com.anshtya.jetx.profile.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for managing user avatar operations.
 *
 * This class provides functionality for retrieving, saving, and updating
 * user avatars. Avatars are stored securely in the app’s private storage,
 * ensuring that user data is isolated from external access.
 */
@Singleton
class AvatarManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getAvatar(
        userId: String,
    ): Uri? {
        return withContext(ioDispatcher) {
            val avatarFile = File(FileUtil.getAvatarDirectory(context), userId)
            if (avatarFile.exists()) avatarFile.toUri() else null
        }
    }

    suspend fun saveAvatar(
        userId: String,
        byteArray: ByteArray
    ): Result<File> = runCatching {
        withContext(ioDispatcher) {
            val file = FileUtil.createFile(
                filePath = FileUtil.getAvatarDirectory(context),
                name = userId,
                ext = "jpg"
            )
            ensureActive()
            FileOutputStream(file, false).use { outputStream ->
                outputStream.write(byteArray)
            }

            file
        }
    }

    suspend fun saveTempAvatar(
        userId: String,
        byteArray: ByteArray
    ): Result<File> = runCatching {
        withContext(ioDispatcher) {
            val tempFile = File.createTempFile(userId, ".jpg", context.cacheDir)
            tempFile.writeBytes(byteArray)
            tempFile
        }
    }

    suspend fun updateAvatar(
        userId: String,
        newByteArray: ByteArray
    ): Result<File> = runCatching {
        withContext(ioDispatcher) {
            val avatarDirectory = FileUtil.getAvatarDirectory(context)
            val avatarFile = File(avatarDirectory, userId)
            ensureActive()
            FileOutputStream(avatarFile, false).use { outputStream ->
                outputStream.write(newByteArray)
            }

            avatarFile
        }
    }

    suspend fun deleteAvatar(
        userId: String
    ): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            val avatarDirectory = FileUtil.getAvatarDirectory(context)
            val avatarFile = File(avatarDirectory, userId)
            ensureActive()
            if (avatarFile.exists()){
                avatarFile.delete()
            }
        }
    }
}