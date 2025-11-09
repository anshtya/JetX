package com.anshtya.jetx.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.core.coroutine.ExternalScope
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReplyReceiver : BroadcastReceiver() {
    @Inject
    lateinit var messagesRepository: MessagesRepository

    @Inject
    @ExternalScope
    lateinit var scope: CoroutineScope

    override fun onReceive(context: Context?, intent: Intent?) {
        val chatId = intent?.getIntExtra(Constants.CHAT_ID_ARG, 0) ?: return
        if (chatId == 0) return

        val replyText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("text_reply")
            .toString()

        val finisher = goAsync()
        scope.launch {
            messagesRepository.markChatMessagesAsSeen(chatId)
            messagesRepository.sendChatMessage(chatId, replyText, attachmentUri = null).getOrThrow()
            finisher.finish()
        }
    }
}