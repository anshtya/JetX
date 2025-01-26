package com.anshtya.jetx.work.worker

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
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
import com.anshtya.jetx.common.model.NetworkIncomingMessage
import com.anshtya.jetx.common.model.toIncomingMessage
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.notifications.NotificationChannels
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json

@HiltWorker
class MessageReceiveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val client: SupabaseClient,
    private val profileRepository: ProfileRepository,
    private val localMessagesDataSource: LocalMessagesDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val message = Json.decodeFromString<NetworkIncomingMessage>(inputData.getString(MESSAGE_KEY)!!)
            val senderId = message.senderId
            val profileExists = profileRepository.profileExists(senderId)
            if (!profileExists) {
                profileRepository.fetchAndSaveProfile(senderId.toString())
            }
            localMessagesDataSource.insertMessage(
                incomingMessage = message.toIncomingMessage(),
                isCurrentUser = false
            )
            client.from(MESSAGE_TABLE).update(
                update = { set("has_received", true) },
                request = {
                    filter { eq("id", message.id) }
                }
            )
            val senderProfile = profileRepository.getProfile(message.senderId)!!

            postMessageNotification(
                senderName = senderProfile.username,
                senderProfilePicture = senderProfile.pictureUrl,
                message = message.text!!
            )

            Result.success()
        } catch (e: Exception) {
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