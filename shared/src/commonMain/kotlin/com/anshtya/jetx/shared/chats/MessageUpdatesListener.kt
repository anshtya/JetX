package com.anshtya.jetx.shared.chats

import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.shared.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.shared.model.MessageStatus
import com.anshtya.jetx.shared.util.Constants.MESSAGE_TABLE
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
import org.koin.core.annotation.Single

@Single
class MessageUpdatesListener(
    client: SupabaseClient,
    private val localMessagesDataSource: LocalMessagesDataSource,
    @param:DefaultScope private val coroutineScope: CoroutineScope
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
                table = MESSAGE_TABLE
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
            localMessagesDataSource.updateMessage(
                messageId = networkMessage.id,
                text = networkMessage.text,
                status = if (networkMessage.hasSeen) {
                    MessageStatus.SEEN
                } else if (networkMessage.hasReceived) {
                    MessageStatus.RECEIVED
                } else null
            )
        }.launchIn(coroutineScope)
    }
}