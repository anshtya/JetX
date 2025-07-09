package com.anshtya.jetx.shared.fcm

import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.SupabaseClient
import org.koin.core.annotation.Single

@Single
expect class FcmTokenManager(
    client: SupabaseClient,
    firebaseMessaging: FirebaseMessaging
) {
    suspend fun addToken()

    suspend fun removeToken()

    suspend fun addTokenToServer(token: String)
}