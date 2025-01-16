package com.anshtya.jetx.chats.data

interface MessageListener {
    suspend fun subscribe()

    suspend fun unsubscribe()
}