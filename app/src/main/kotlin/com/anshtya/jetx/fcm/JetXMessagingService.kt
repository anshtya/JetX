package com.anshtya.jetx.fcm

import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.shared.fcm.FcmTokenManager
import com.anshtya.jetx.work.WorkScheduler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject

class JetXMessagingService : FirebaseMessagingService() {
    private val workScheduler: WorkScheduler by inject()
    private val fcmTokenManager: FcmTokenManager by inject()
    @DefaultScope private val coroutineScope: CoroutineScope by inject()

    private var job: Job? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        job = coroutineScope.launch {
            fcmTokenManager.addTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(Json.encodeToString(messageData))
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}