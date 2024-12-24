package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.fake.FakeChatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChatListRepository(
        impl: FakeChatsRepository
    ): ChatsRepository
}