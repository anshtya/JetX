package com.anshtya.jetx.fcm

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.anshtya.jetx.R
import com.anshtya.jetx.notifications.NotificationChannels
import com.anshtya.jetx.work.WorkScheduler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class JetXMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var workScheduler: WorkScheduler

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        postCheckingForNewMessages()
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(
            messageId = UUID.fromString(messageData["id"]),
            encodedMessage = Json.encodeToString(messageData)
        )
    }

    private fun postCheckingForNewMessages() {
        val builder = NotificationCompat.Builder(this, NotificationChannels.MESSAGE_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.checking_for_new_messages))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}