package com.anshtya.jetx.preferences

interface PreferencesStore {
    suspend fun <T> get(key: String): T?

    suspend fun <T> set(key: String, value: T)
}