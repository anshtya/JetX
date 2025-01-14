package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.MessageRepository
import com.anshtya.jetx.chats.data.MessageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl
    ): MessageRepository
}