package com.anshtya.jetx.auth.data

import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.work.WorkManagerHelper
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
    private val preferencesStore: PreferencesStore,
    private val db: JetXDatabase,
    private val messageUpdatesListener: MessageUpdatesListener,
    private val workManagerHelper: WorkManagerHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val tag = this::class.simpleName

    suspend fun performLocalCleanup(): Result<Unit> =
        runCatching {
            workManagerHelper.cancelAllWork()
            withContext(ioDispatcher) {
                db.clearAllTables()
            }

            preferencesStore.clearPreferences()
            messageUpdatesListener.unsubscribe()
        }
}