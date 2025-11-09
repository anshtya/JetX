package com.anshtya.jetx.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.core.coroutine.ExternalScope
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkAsReadReceiver: BroadcastReceiver() {
    private val tag = this::class.simpleName

    @Inject
    lateinit var messagesRepository: MessagesRepository

    @Inject
    lateinit var defaultNotificationManager: DefaultNotificationManager

    @Inject
    @ExternalScope
    lateinit var scope: CoroutineScope
            
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(tag, "Broadcast received")

        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return

        val notificationId = NotificationIds.getMessageNotificationId(chatId)
        defaultNotificationManager.cancelNotification(notificationId)

        val finisher = goAsync()
        scope.launch {
            messagesRepository.markChatMessagesAsSeen(chatId)
            finisher.finish()
        }
    }
}