package com.anshtya.jetx

import com.anshtya.jetx.notifications.di.NotificationManagerModule
import com.anshtya.jetx.shared.SharedModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        SharedModule::class,
        NotificationManagerModule::class
    ]
)
@ComponentScan
class AppModule