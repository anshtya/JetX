package com.anshtya.jetx.chats.data

import android.util.Log
import com.anshtya.jetx.chats.data.model.NetworkMessage
import com.anshtya.jetx.chats.data.model.toEntity
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.profile.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MessageListenerImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val profileRepository: ProfileRepository,
    @DefaultScope private val coroutineScope: CoroutineScope
) : MessageListener {
    private val json = Json
    private val userId = client.auth.currentSessionOrNull()?.user?.id
        ?: throw IllegalStateException("User should be logged in to access chats")

    private val messagesChannel = client.channel("messages-db-changes")
    private val messageChanges = messagesChannel.postgresChangeFlow<PostgresAction>(
        schema = "public",
        filter = {
            table = "messages"
            filter(
                filter = FilterOperation(
                    column = "recipient_id",
                    operator = FilterOperator.EQ,
                    value = userId
                )
            )
        }
    )

    override suspend fun subscribe() {
        messagesChannel.subscribe()
        listenMessages()
    }

    override suspend fun unsubscribe() {
        messagesChannel.unsubscribe()
    }

    private fun listenMessages() {
        coroutineScope.launch {
            messageChanges.collect { action ->
                Log.d("foo", "$action")
                when (action) {
                    is PostgresAction.Insert -> {
                        val message = json.decodeFromString<NetworkMessage>(
                            string = action.record.toString()
                        )
                        saveMessage(message)
                    }

                    is PostgresAction.Update -> {
                        val message = json.decodeFromString<NetworkMessage>(
                            string = action.record.toString()
                        )
                        saveMessage(message)
                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun saveMessage(networkMessage: NetworkMessage) {
        val senderId = networkMessage.senderId
        val chat = chatDao.getChat(senderId)
        val chatId = if (chat != null) {
            chat.id
        } else {
            profileRepository.fetchAndSaveProfile(senderId.toString())

            // Logged-in user will chat to sender, hence sender will be recipient.
            chatDao.insertChat(ChatEntity(recipientId = senderId)).toInt()
        }
        messageDao.upsertMessage(networkMessage.toEntity(chatId))
    }
}