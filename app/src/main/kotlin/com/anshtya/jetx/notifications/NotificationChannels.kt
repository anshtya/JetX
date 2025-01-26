package com.anshtya.jetx.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import com.anshtya.jetx.R

class NotificationChannels {
    companion object {
        const val CHAT_GROUP = "chat"
        const val OTHER_GROUP = "other"

        const val MESSAGE_CHANNEL = "message"
        const val OTHER_CHANNEL = "other"

        fun getInstance() = NotificationChannels()
    }

    fun onCreate(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val chatNotificationGroup = NotificationChannelGroup(
            CHAT_GROUP,
            context.getString(R.string.notification_chat_group)
        )
        val otherNotificationGroup = NotificationChannelGroup(
            OTHER_GROUP,
            context.getString(R.string.notification_other_group)
        )

        val messageNotificationChannel = NotificationChannel(
            MESSAGE_CHANNEL,
            context.getString(R.string.notification_message_channel),
            IMPORTANCE_HIGH
        )
        val otherNotificationChannel = NotificationChannel(
            OTHER_CHANNEL,
            context.getString(R.string.notification_other_channel),
            IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannelGroups(
            listOf(
                chatNotificationGroup,
                otherNotificationGroup
            )
        )
        manager.createNotificationChannels(
            listOf(
                messageNotificationChannel,
                otherNotificationChannel
            )
        )
    }
}