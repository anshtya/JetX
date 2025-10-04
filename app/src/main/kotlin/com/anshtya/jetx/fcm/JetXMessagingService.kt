package com.anshtya.jetx.fcm

import com.anshtya.jetx.work.WorkScheduler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class JetXMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var workScheduler: WorkScheduler

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        runBlocking {
            fcmTokenManager.addTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(Json.encodeToString(messageData))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}