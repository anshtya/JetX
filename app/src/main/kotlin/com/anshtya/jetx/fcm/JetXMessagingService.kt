package com.anshtya.jetx.fcm

import android.util.Log
import androidx.work.WorkManager
import com.anshtya.jetx.work.WorkScheduler
import com.anshtya.jetx.work.worker.FcmRefreshWorker
import com.anshtya.jetx.work.worker.MessageReceiveWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class JetXMessagingService : FirebaseMessagingService() {
    // TODO: remove this
    @Inject
    lateinit var workScheduler: WorkScheduler

    @Inject
    lateinit var workManager: WorkManager

    private val tag = this::class.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(tag, "New token received")
        FcmRefreshWorker.scheduleWork(workManager)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(tag, "Message received")
        val messageData = remoteMessage.data
        workScheduler.createMessageReceiveWork(Json.encodeToString(messageData))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}