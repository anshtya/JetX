package com.anshtya.jetx.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.net.toUri
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.R
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val profileRepository: ProfileRepository
) {
    private var currentChatId: Int? = null
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun setCurrentChat(id: Int) {
        currentChatId = id
    }

    fun clearCurrentChat() {
        currentChatId = null
    }

    suspend fun postChatNotification(
        messageId: Int
    ) {
        val savedMessage = localMessagesDataSource.getMessage(messageId)
        if (currentChatId == savedMessage.chatId) return

        val senderProfile = profileRepository.getProfile(savedMessage.senderId)

        createNotification(
            chatId = savedMessage.chatId,
            senderName = senderProfile.name,
            messages = localMessagesDataSource.getUnreadRecentMessages(savedMessage.chatId),
            unreadChats = localMessagesDataSource.getUnreadChatsCount(),
            unreadMessages = localMessagesDataSource.getUnreadMessagesCount()
        )
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    private fun createNotification(
        chatId: Int,
        senderName: String,
        messages: List<String>,
        unreadChats: Int,
        unreadMessages: Int,
    ) {
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

        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            context,
            chatId,
            Intent(context, MarkAsReadReceiver::class.java).apply {
                putExtra(Constants.CHAT_ID_ARG, chatId)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            context.getString(R.string.reply),
            PendingIntent.getBroadcast(
                context, chatId,
                Intent(context, ReplyReceiver::class.java).apply {
                    putExtra(Constants.CHAT_ID_ARG, chatId)
                    putExtra(Constants.CHAT_TITLE, senderName)
                },
                PendingIntent.FLAG_MUTABLE
            )
        ).addRemoteInput(
            RemoteInput.Builder("text_reply").run {
                setLabel(context.getString(R.string.reply))
                build()
            }
        ).build()

        val builder = NotificationCompat.Builder(
            context, NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText(messages.first())
            .setAutoCancel(true)
            .setGroup(MESSAGE_GROUP)
            .setStyle(
                NotificationCompat.InboxStyle().also {
                    messages.forEach { text ->
                        it.addLine(text)
                    }
                }
            )
            .setContentIntent(resultPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.mark_as_read),
                markAsReadPendingIntent
            )
            .addAction(replyAction)

        val notificationId = NotificationIds.getMessageNotificationId(chatId)
        notificationManager.notify(notificationId, builder.build())

        if (unreadChats > 1) {
            updateSummary(
                unreadChats = unreadChats,
                unreadMessages = unreadMessages
            )
        }
    }

    private fun updateSummary(
        unreadChats: Int,
        unreadMessages: Int
    ) {
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            NotificationIds.MESSAGE_SUMMARY,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            context, NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText(
                        context.getString(
                            R.string.multiple_chat_messages,
                            unreadMessages.toString(),
                            unreadChats.toString()
                        )
                    )
            )
            .setGroup(MESSAGE_GROUP)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)

        notificationManager.notify(NotificationIds.MESSAGE_SUMMARY, builder.build())
    }

    companion object {
        private const val MESSAGE_GROUP = "messages"
    }
}