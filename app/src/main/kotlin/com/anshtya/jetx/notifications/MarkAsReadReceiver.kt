package com.anshtya.jetx.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MarkAsReadReceiver: BroadcastReceiver() {
    private val messagesRepository: MessagesRepository by inject(MessagesRepository::class.java)
    private val notificationManager: NotificationManager by inject(NotificationManager::class.java)

    @DefaultScope
    private val coroutineScope: CoroutineScope by inject(CoroutineScope::class.java)
            
    override fun onReceive(context: Context?, intent: Intent?) {
        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return

        notificationManager.cancel(chatId)

        val finisher = goAsync()
        coroutineScope.launch {
            messagesRepository.markChatMessagesAsSeen(chatId)
            finisher.finish()
        }
    }
}