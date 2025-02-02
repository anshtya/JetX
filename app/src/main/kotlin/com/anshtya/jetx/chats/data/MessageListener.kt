package com.anshtya.jetx.chats.data

import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.SharedFlow

interface MessageListener {
    val changes: SharedFlow<PostgresAction>

    suspend fun subscribe()

    suspend fun unsubscribe()
}