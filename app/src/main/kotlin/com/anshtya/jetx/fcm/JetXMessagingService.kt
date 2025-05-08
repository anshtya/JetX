package com.anshtya.jetx.fcm

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
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(
            messageId = UUID.fromString(messageData["id"]),
            encodedMessage = Json.encodeToString(messageData)
        )
    }
}