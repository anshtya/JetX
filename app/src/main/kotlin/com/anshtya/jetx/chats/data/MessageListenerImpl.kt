package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.coroutine.DefaultScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MessageListenerImpl @Inject constructor(
    client: SupabaseClient,
    @DefaultScope private val coroutineScope: CoroutineScope
) : MessageListener {
    private val supabaseAuth = client.auth
    private val messagesChannel = client.channel("messages-db-changes")

    private val _changes = MutableSharedFlow<PostgresAction>()
    override val changes = _changes.asSharedFlow()

    override suspend fun subscribe() {
        val userId = supabaseAuth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User should be logged in to observe messages")
        listenMessages(messagesChannel, userId)
        listenSenderMessageUpdates(messagesChannel, userId)
        messagesChannel.subscribe()
    }

    override suspend fun unsubscribe() {
        messagesChannel.unsubscribe()
    }

    private fun listenMessages(channel: RealtimeChannel, userId: String) {
        channel.postgresChangeFlow<PostgresAction>(
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
        ).onEach { action -> _changes.emit(action) }.launchIn(coroutineScope)
    }

    private fun listenSenderMessageUpdates(channel: RealtimeChannel, userId: String) {
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
        ).onEach { action -> _changes.emit(action) }.launchIn(coroutineScope)
    }
}