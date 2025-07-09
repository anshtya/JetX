package com.anshtya.jetx.notifications.di

import android.app.NotificationManager
import android.content.Context
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NotificationManagerModule {
    @Single
    fun provideNotificationManagerModule(context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}