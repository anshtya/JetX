package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.MessageListener
import com.anshtya.jetx.chats.data.MessageListenerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class MessageListenerModule {
    @Binds
    @ViewModelScoped
    abstract fun bindMessageListener(
        impl: MessageListenerImpl
    ) : MessageListener
}