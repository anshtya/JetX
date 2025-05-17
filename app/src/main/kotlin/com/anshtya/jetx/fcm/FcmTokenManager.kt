package com.anshtya.jetx.fcm

import com.anshtya.jetx.util.Constants.PROFILE_TABLE
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FcmTokenManager @Inject constructor(
    private val client: SupabaseClient,
    private val firebaseMessaging: FirebaseMessaging
) {
    private val profileTable = client.from(PROFILE_TABLE)

    suspend fun addToken() {
        val userId = client.auth.currentUserOrNull()?.id!!
        val fcmToken = firebaseMessaging.token.await()
        addTokenToServer(token = fcmToken, userId = userId)
    }

    suspend fun removeToken() {
        val userId = client.auth.currentUserOrNull()?.id!!
        profileTable.update(
            update = { set<Any>("fcm_token", null) },
            request = {
                filter { eq("user_id", userId) }
            }
        )
    }

    suspend fun addTokenToServer(token: String) {
        val userId = client.auth.currentUserOrNull()?.id
        userId?.let { addTokenToServer(token = token, userId = userId) }
    }

    private suspend fun addTokenToServer(
        token: String,
        userId: String
    ) {
        profileTable.update(
            update = { set("fcm_token", token) },
            request = {
                filter { eq("user_id", userId) }
            }
        )
    }
}