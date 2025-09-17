package com.anshtya.jetx.auth.di

import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.AuthRepositoryImpl
import com.anshtya.jetx.auth.data.LogoutManager
import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.core.coroutine.DefaultScope
import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.network.api.AuthApi
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.work.WorkManagerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthService(
        authApi: AuthApi
    ): AuthService {
        return AuthService(
            authApi = authApi
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(
        authService: AuthService,
        tokenStore: TokenStore,
        logoutManager: LogoutManager,
        @DefaultScope defaultScope: CoroutineScope
    ): AuthRepositoryImpl {
        return AuthRepositoryImpl(
            authService = authService,
            tokenStore = tokenStore,
            logoutManager = logoutManager,
            scope = defaultScope
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideLogoutManager(
        tokenStore: TokenStore,
        preferencesStore: PreferencesStore,
        db: JetXDatabase,
        messageUpdatesListener: MessageUpdatesListener,
        workManagerHelper: WorkManagerHelper,
    ): LogoutManager {
        return LogoutManager(
            tokenStore = tokenStore,
            preferencesStore = preferencesStore,
            db = db,
            messageUpdatesListener = messageUpdatesListener,
            workManagerHelper = workManagerHelper
        )
    }
}