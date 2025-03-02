package com.anshtya.jetx.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkAsReadReceiver: BroadcastReceiver() {
    @Inject
    lateinit var localMessagesDataSource: LocalMessagesDataSource

    @Inject
    @DefaultScope
    lateinit var coroutineScope: CoroutineScope
            
    override fun onReceive(context: Context?, intent: Intent?) {
        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return

        (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(chatId)

        val finisher = goAsync()
        coroutineScope.launch {
            localMessagesDataSource.markChatMessagesAsSeen(chatId)
            finisher.finish()
        }
    }
}