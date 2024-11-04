package com.anshtya.jetx.di

import com.anshtya.jetx.BuildConfig
import com.anshtya.jetx.data.preferences.auth.AuthTokenManager
import com.anshtya.jetx.data.network.interceptor.AuthorizationInterceptor
import com.anshtya.jetx.data.network.authenticator.TokenRefreshAuthenticator
import com.anshtya.jetx.data.network.interceptor.httpLoggingInterceptor
import com.anshtya.jetx.data.network.service.AuthService
import com.anshtya.jetx.data.network.service.MainService
import com.anshtya.jetx.data.network.service.RefreshTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideAuthService(
        clientBuilder: OkHttpClient.Builder,
        retrofitBuilder: Retrofit.Builder
    ): AuthService {
        return retrofitBuilder
            .client(clientBuilder.build())
            .build()
            .create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideRefreshTokenService(
        clientBuilder: OkHttpClient.Builder,
        retrofitBuilder: Retrofit.Builder
    ): RefreshTokenService {
        return retrofitBuilder
            .client(clientBuilder.build())
            .build()
            .create(RefreshTokenService::class.java)
    }

    @Singleton
    @Provides
    fun provideMainService(
        clientBuilder: OkHttpClient.Builder,
        retrofitBuilder: Retrofit.Builder,
        authorizationInterceptor: AuthorizationInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator
    ): MainService {
        val client = clientBuilder
            .addInterceptor(authorizationInterceptor)
            .authenticator(tokenRefreshAuthenticator)
            .build()

        return retrofitBuilder
            .client(client)
            .build()
            .create(MainService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
    }

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(
        authTokenManager: AuthTokenManager
    ): AuthorizationInterceptor =
        AuthorizationInterceptor(authTokenManager)

    @Singleton
    @Provides
    fun provideTokenRefreshAuthenticator(
        refreshTokenService: RefreshTokenService,
        authTokenManager: AuthTokenManager
    ): TokenRefreshAuthenticator =
        TokenRefreshAuthenticator(refreshTokenService, authTokenManager)
}