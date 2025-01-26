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
        profileTable.update(
            update = { set("fcm_token", fcmToken) },
            request = {
                filter { eq("user_id", userId) }
            }
        )
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
}