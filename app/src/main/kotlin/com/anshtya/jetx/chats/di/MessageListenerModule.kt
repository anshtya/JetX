package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.MessageListener
import com.anshtya.jetx.chats.data.MessageListenerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageListenerModule {
    @Binds
    @Singleton
    abstract fun bindMessageListener(
        impl: MessageListenerImpl
    ) : MessageListener
}