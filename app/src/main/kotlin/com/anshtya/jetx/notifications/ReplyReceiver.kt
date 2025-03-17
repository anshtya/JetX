package com.anshtya.jetx.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReplyReceiver: BroadcastReceiver() {
    @Inject
    lateinit var messagesRepository: MessagesRepository

    @Inject
    @DefaultScope
    lateinit var coroutineScope: CoroutineScope

    override fun onReceive(context: Context?, intent: Intent?) {
        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return
        val senderName = intent.getStringExtra(Constants.CHAT_TITLE) ?: return

        val replyText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("text_reply")
            .toString()

        val repliedNotification = NotificationCompat.Builder(
            context!!, NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText("You: $replyText")
            .setSilent(true)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(chatId, repliedNotification)

        val finisher = goAsync()
        coroutineScope.launch {
            messagesRepository.markChatMessagesAsSeen(chatId)
            messagesRepository.sendChatMessage(chatId, replyText)
            finisher.finish()
        }
    }
}