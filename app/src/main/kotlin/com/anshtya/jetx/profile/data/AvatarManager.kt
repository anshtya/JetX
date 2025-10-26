package com.anshtya.jetx.profile.data

import android.content.Context
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
    suspend fun saveAvatar(
        userId: String,
        byteArray: ByteArray,
        ext: String,
    ): Result<File> = runCatching {
        withContext(ioDispatcher) {
            val file = FileUtil.createFile(
                filePath = FileUtil.getAvatarDirectory(context),
                name = userId,
                ext = ext
            )
            ensureActive()
            FileOutputStream(file, false).use { outputStream ->
                outputStream.write(byteArray)
            }

            file
        }
    }

    suspend fun updateAvatar(
        userId: String,
        newByteArray: ByteArray,
        ext: String
    ): Result<File> = runCatching {
        withContext(ioDispatcher) {
            val avatarFile = FileUtil.createFile(
                filePath = FileUtil.getAvatarDirectory(context),
                name = userId,
                ext = ext
            )
            ensureActive()
            FileOutputStream(avatarFile, false).use { outputStream ->
                outputStream.write(newByteArray)
            }

            avatarFile
        }
    }

    suspend fun deleteAvatar(
        path: String
    ): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            File(path).delete()
        }
    }

    suspend fun clearAll(): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            val directory = FileUtil.getAvatarDirectory(context)
            if (directory.exists()) {
                directory.deleteRecursively()
            }
        }
    }
}