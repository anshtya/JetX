package com.anshtya.jetx.work.worker

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.IconCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.MessageReceiveRepository
import com.anshtya.jetx.work.model.NetworkIncomingMessage
import com.anshtya.jetx.notifications.NotificationChannels
import com.anshtya.jetx.profile.ProfileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class MessageReceiveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val profileRepository: ProfileRepository,
    private val messageReceiveRepository: MessageReceiveRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val message = Json.decodeFromString<NetworkIncomingMessage>(inputData.getString(MESSAGE_KEY)!!)
            messageReceiveRepository.insertChatMessage(
                id = message.id,
                senderId = message.senderId,
                recipientId = message.recipientId,
                text = message.text,
                attachmentUri = null
            )

            val senderProfile = profileRepository.getProfile(message.senderId)!!
            postMessageNotification(
                senderName = senderProfile.username,
                senderProfilePicture = senderProfile.pictureUrl,
                message = message.text!!
            )

            Result.success()
        } catch (e: Exception) {
            Log.e("work", "$e")
            postMayHaveNewMessages()
            Result.retry()
        }
    }

    private suspend fun postMessageNotification(
        senderName: String,
        senderProfilePicture: String?,
        message: String
    ) {
        var profilePicture: Bitmap? = null
        if (senderProfilePicture != null) {
            val imageLoader = ImageLoader(applicationContext)
            val request = ImageRequest.Builder(applicationContext)
                .data(senderProfilePicture)
                .build()

            profilePicture = (imageLoader.execute(request) as SuccessResult).image.toBitmap()
        }

        val builder = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(
                if (profilePicture != null) {
                    IconCompat.createWithBitmap(profilePicture)
                } else {
                    IconCompat.createWithResource(
                        applicationContext,
                        R.drawable.blank_profile_picture
                    )
                }
            )
            .setContentTitle(senderName)
            .setContentText(message)

        val notificationManager = getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.notify(1, builder.build())
    }

    private fun postMayHaveNewMessages() {
        val builder = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(applicationContext, R.string.app_name))
            .setContentText(getString(applicationContext, R.string.may_have_new_messages))

        val notificationManager = getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.notify(1, builder.build())
    }

    companion object {
        const val WORKER_NAME = "message_receive"
        const val MESSAGE_KEY = "message_key"
    }
}