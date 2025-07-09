package com.anshtya.jetx.shared.fcm.di

import com.google.firebase.messaging.FirebaseMessaging
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class FirebaseMessagingModule {
    @Single
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}