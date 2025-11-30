package com.anshtya.jetx.auth.data

import android.util.Log
import androidx.work.WorkManager
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.network.websocket.WebSocketManager
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.profile.data.AvatarManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles the cleanup operations required during user logout.
 *
 * This class is responsible for removing user data from local storage,
 * cancelling background tasks, and unsubscribing from message updates.
 *
 */
@Singleton
class LogoutManager @Inject constructor(
    private val store: JetxPreferencesStore,
    private val db: JetXDatabase,
    private val webSocketManager: WebSocketManager,
    private val avatarManager: AvatarManager,
    private val workManager: WorkManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val tag = this::class.simpleName

    suspend fun performLocalCleanup() {
        try {
            workManager.cancelAllWork()
            withContext(ioDispatcher) {
                db.clearAllTables()
            }

            store.user.clear()
            avatarManager.clearAll()
            webSocketManager.disconnect()
        } catch (e: Exception) {
            Log.e(tag, "Failed to clear local user data", e)
        }
    }
}