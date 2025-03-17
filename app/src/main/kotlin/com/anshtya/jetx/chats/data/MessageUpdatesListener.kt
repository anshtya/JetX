package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.NetworkMessage
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.dao.MessageDao
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MessageUpdatesListener @Inject constructor(
    client: SupabaseClient,
    private val messageDao: MessageDao,
    @DefaultScope private val coroutineScope: CoroutineScope
) {
    private val supabaseAuth = client.auth
    private val messagesChannel = client.channel("messages-db-changes")

    suspend fun subscribe() {
        val userId = supabaseAuth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User should be logged in to observe messages")
        listenSentMessageUpdates(messagesChannel, userId)
        messagesChannel.subscribe()
    }

    suspend fun unsubscribe() {
        messagesChannel.unsubscribe()
    }

    private fun listenSentMessageUpdates(channel: RealtimeChannel, userId: String) {
        channel.postgresChangeFlow<PostgresAction.Update>(
            schema = "public",
            filter = {
                table = "messages"
                filter(
                    filter = FilterOperation(
                        column = "sender_id",
                        operator = FilterOperator.EQ,
                        value = userId
                    )
                )
            }
        ).onEach { action ->
            val networkMessage = action.decodeRecord<NetworkMessage>()
            val storedMessage = messageDao.getChatMessage(networkMessage.id)
            messageDao.upsertMessage(
                storedMessage.copy(
                    text = networkMessage.text,
                    status = if (networkMessage.hasSeen) {
                        MessageStatus.SEEN
                    } else if (networkMessage.hasReceived) {
                        MessageStatus.RECEIVED
                    } else storedMessage.status
                )
            )
        }.launchIn(coroutineScope)
    }
}