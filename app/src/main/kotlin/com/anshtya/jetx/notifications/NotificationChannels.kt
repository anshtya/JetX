package com.anshtya.jetx.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.anshtya.jetx.R

object NotificationChannels {
    const val CHAT_GROUP = "chat"
    const val OTHER_GROUP = "other"

    const val MESSAGE_CHANNEL = "message"
    const val OTHER_CHANNEL = "other"


    fun create(context: Context) {
        val chatNotificationGroup = NotificationChannelGroup(
            CHAT_GROUP,
            context.getString(R.string.notification_chat_group)
        )
        val otherNotificationGroup = NotificationChannelGroup(
            OTHER_GROUP,
            context.getString(R.string.notification_other_group)
        )
        NotificationManagerCompat.from(context).createNotificationChannelGroups(
            listOf(chatNotificationGroup, otherNotificationGroup)
        )

        val messageNotificationChannel = NotificationChannel(
            MESSAGE_CHANNEL,
            context.getString(R.string.notification_message_channel),
            IMPORTANCE_HIGH
        ).apply {
            group = chatNotificationGroup.id
        }
        val otherNotificationChannel = NotificationChannel(
            OTHER_CHANNEL,
            context.getString(R.string.notification_other_channel),
            IMPORTANCE_DEFAULT
        ).apply {
            group = otherNotificationGroup.id
        }
        NotificationManagerCompat.from(context).createNotificationChannels(
            listOf(messageNotificationChannel, otherNotificationChannel)
        )
    }
}