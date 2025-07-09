package com.anshtya.jetx.shared.supabase

import com.anshtya.jetx.shared.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SupabaseModule {
    @Single
    fun provideSupabaseClient(): SupabaseClient {
        val url = if (BuildKonfig.DEBUG) BuildKonfig.DEBUG_URL else BuildKonfig.RELEASE_URL
        val key = if (BuildKonfig.DEBUG) BuildKonfig.DEBUG_KEY else BuildKonfig.RELEASE_KEY
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            if (BuildKonfig.DEBUG) {
                defaultLogLevel = LogLevel.DEBUG
            }
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}