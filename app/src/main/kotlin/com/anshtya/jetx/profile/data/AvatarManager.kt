package com.anshtya.jetx.profile.data

import android.content.Context
import android.util.Log
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.IOException
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
    private val tag = this::class.simpleName

    suspend fun saveAvatar(
        userId: String,
        byteArray: ByteArray,
        ext: String,
    ): Result<File> = try {
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

            Result.success(file)
        }
    } catch (e: IOException) {
        Log.e(tag, "Failed to save avatar", e)
        Result.failure(e)
    }

    suspend fun updateAvatar(
        userId: String,
        newByteArray: ByteArray,
        ext: String
    ): Result<File> = try {
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

            Result.success(avatarFile)
        }
    } catch (e: IOException) {
        Log.e(tag, "Failed to update avatar", e)
        Result.failure(e)
    }

    suspend fun deleteAvatar(
        path: String
    ) {
        try {
            withContext(ioDispatcher) {
                File(path).delete()
            }
        } catch (e: IOException) {
            Log.e(tag, "Failed to delete avatar", e)
        }
    }

    suspend fun clearAll() {
        try {
            withContext(ioDispatcher) {
                val directory = FileUtil.getAvatarDirectory(context)
                if (directory.exists()) {
                    directory.deleteRecursively()
                }
            }
        } catch (e: IOException) {
            Log.e(tag, "Failed to delete avatar directory", e)
        }
    }
}