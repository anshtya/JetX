package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.MessageReceiveRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.chats.data.MessagesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMessagesRepository(
        impl: MessagesRepositoryImpl
    ): MessagesRepository

    @Binds
    @Singleton
    abstract fun bindMessageReceiveRepository(
        impl: MessagesRepositoryImpl
    ): MessageReceiveRepository
}