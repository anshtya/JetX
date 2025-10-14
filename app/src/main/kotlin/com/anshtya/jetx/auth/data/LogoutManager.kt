package com.anshtya.jetx.auth.data

import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.core.coroutine.IoDispatcher
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.work.WorkManagerHelper
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
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
    private val messageUpdatesListener: MessageUpdatesListener,
    private val workManagerHelper: WorkManagerHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun performLocalCleanup(): Result<Unit> =
        runCatching {
            workManagerHelper.cancelAllWork()
            withContext(ioDispatcher) {
                db.clearAllTables()
            }

            store.user.clear()
            messageUpdatesListener.unsubscribe()
        }
}