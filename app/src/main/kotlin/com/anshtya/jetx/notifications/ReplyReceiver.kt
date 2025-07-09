package com.anshtya.jetx.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.net.toUri
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class ReplyReceiver : BroadcastReceiver() {
    private val messagesRepository: MessagesRepository by inject(MessagesRepository::class.java)
    private val notificationManager: NotificationManager by inject(NotificationManager::class.java)

    @DefaultScope
    private val coroutineScope: CoroutineScope by inject(CoroutineScope::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return
        val senderName = intent.getStringExtra(Constants.CHAT_TITLE) ?: return

        val replyText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("text_reply")
            .toString()

        val resultIntent = Intent(context, MainActivity::class.java).apply {
            data =
                "${Constants.BASE_APP_URL}/${Constants.CHAT_ARG}?${Constants.CHAT_ID_ARG}=$chatId"
                    .toUri()
        }
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            chatId,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val repliedNotification = NotificationCompat.Builder(
            context!!, NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText("You: $replyText")
            .setSilent(true)
            .setContentIntent(resultPendingIntent)
            .build()

        notificationManager.notify(chatId, repliedNotification)

        val finisher = goAsync()
        coroutineScope.launch {
            messagesRepository.markChatMessagesAsSeen(chatId)
            messagesRepository.sendChatMessage(chatId, replyText, attachmentUri = null).getOrThrow()
            finisher.finish()
        }
    }
}