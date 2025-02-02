package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.ChatsRepositoryImpl
import com.anshtya.jetx.chats.data.MessageReceiveRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageReceiveRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMessageReceiveRepository(
        impl: ChatsRepositoryImpl
    ): MessageReceiveRepository
}