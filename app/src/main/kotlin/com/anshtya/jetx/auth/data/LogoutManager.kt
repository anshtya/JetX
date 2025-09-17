package com.anshtya.jetx.auth.data

import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.work.WorkManagerHelper

/**
 * Handles the cleanup operations required during user logout.
 *
 * This class is responsible for removing user data from local storage,
 * cancelling background tasks, and unsubscribing from message updates.
 *
 */
class LogoutManager(
    private val tokenStore: TokenStore,
    private val preferencesStore: PreferencesStore,
    private val db: JetXDatabase,
    private val messageUpdatesListener: MessageUpdatesListener,
    private val workManagerHelper: WorkManagerHelper,
) {
    suspend fun performLocalCleanup() {
        workManagerHelper.cancelAllWork()
        db.clearAllTables()
        tokenStore.clearTokenStore()

        preferencesStore.clearPreferences()
        messageUpdatesListener.unsubscribe()
    }
}