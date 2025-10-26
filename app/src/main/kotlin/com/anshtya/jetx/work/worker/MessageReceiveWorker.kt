package com.anshtya.jetx.work.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.anshtya.jetx.MainActivity
import com.anshtya.jetx.R
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.core.database.dao.MessageDao
import com.anshtya.jetx.core.model.MessageType
import com.anshtya.jetx.core.network.model.NetworkMessage
import com.anshtya.jetx.notifications.MarkAsReadReceiver
import com.anshtya.jetx.notifications.NotificationChannels
import com.anshtya.jetx.notifications.ReplyReceiver
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.util.Constants
import com.anshtya.jetx.work.util.createInputData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class MessageReceiveWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val chatsRepository: ChatsRepository,
    private val profileRepository: ProfileRepository,
    private val messagesRepository: MessagesRepository,
    private val messageDao: MessageDao,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParams) {
    private val tag = this::class.simpleName

    override suspend fun doWork(): Result {
        return try {
            val incomingMessage = NetworkMessage(
                id = UUID.fromString(inputData.getString("id")),
                senderId = UUID.fromString(inputData.getString("senderId")),
                targetId = UUID.fromString(inputData.getString("targetId")),
                type = MessageType.INDIVIDUAL,
                content = inputData.getString("content"),
                attachmentId = inputData.getString("attachmentId").takeIf { it != "null" }
                    ?.let { UUID.fromString(it) }
            )

            val chatId = messagesRepository.receiveChatMessage(
                id = incomingMessage.id,
                senderId = incomingMessage.senderId,
                recipientId = incomingMessage.targetId,
                text = incomingMessage.content,
                attachmentId = incomingMessage.attachmentId
            ).getOrThrow()

            if (chatsRepository.currentChatId != chatId) {
                val senderProfile = profileRepository.getProfile(incomingMessage.senderId)
                postMessageNotification(
                    senderName = senderProfile.username,
                    message = messageDao.getMessage(incomingMessage.id).text!!,
                    chatId = chatId
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.w(tag, e.message, e)
            postMayHaveNewMessages()
            Result.retry()
        }
    }

    private fun postMessageNotification(
        senderName: String,
        message: String,
        chatId: Int
    ) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
            data =
                "${Constants.BASE_APP_URL}/${Constants.CHAT_ARG}?${Constants.CHAT_ID_ARG}=$chatId"
                    .toUri()
        }
        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext,
            chatId,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            chatId,
            Intent(applicationContext, MarkAsReadReceiver::class.java).apply {
                putExtra(Constants.CHAT_ID_ARG, chatId)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.reply),
            PendingIntent.getBroadcast(
                applicationContext, chatId,
                Intent(applicationContext, ReplyReceiver::class.java).apply {
                    putExtra(Constants.CHAT_ID_ARG, chatId)
                    putExtra(Constants.CHAT_TITLE, senderName)
                },
                PendingIntent.FLAG_MUTABLE
            )
        ).addRemoteInput(
            RemoteInput.Builder("text_reply").run {
                setLabel(applicationContext.getString(R.string.reply))
                build()
            }
        ).build()

        val builder = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText(message)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                applicationContext.getString(R.string.mark_as_read),
                markAsReadPendingIntent
            )
            .addAction(replyAction)


        with(notificationManager) {
            cancel(0)
            notify(chatId, builder.build())
        }
    }

    private fun postMayHaveNewMessages() {
        val builder = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.MESSAGE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(applicationContext, R.string.app_name))
            .setContentText(getString(applicationContext, R.string.may_have_new_messages))

        notificationManager.notify(0, builder.build())
    }

    companion object {
        fun scheduleWork(
            workManager: WorkManager,
            data: Map<String, String>
        ) {
            val messageId = data["id"]!!
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(MessageReceiveWorker::class)
                .setInputData(createInputData(data))
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                "message_receive_$messageId",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )
        }
    }
}