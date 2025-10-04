package com.anshtya.jetx.fcm

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Implement
@Singleton
class FcmTokenManager @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) {


    suspend fun getToken(): String {
        return firebaseMessaging.token.await()
    }

    suspend fun addTokenToServer(token: String) {

    }

    private suspend fun addTokenToServer(
        token: String,
        userId: String
    ) {

    }
}