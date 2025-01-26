package com.anshtya.jetx.fcm.di

import com.anshtya.jetx.fcm.FcmTokenManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FcmTokenManagerModule {
    @Provides
    @Singleton
    fun provideFcmTokenManager(client: SupabaseClient): FcmTokenManager {
        return FcmTokenManager(
            client = client,
            firebaseMessaging = FirebaseMessaging.getInstance()
        )
    }
}