package com.anshtya.jetx.fcm

import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.work.WorkScheduler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class JetXMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var workScheduler: WorkScheduler

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    @Inject
    @DefaultScope
    lateinit var coroutineScope: CoroutineScope

    private var job: Job? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        job = coroutineScope.launch {
            fcmTokenManager.addTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(
            messageId = UUID.fromString(messageData["id"]),
            encodedMessage = Json.encodeToString(messageData)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}