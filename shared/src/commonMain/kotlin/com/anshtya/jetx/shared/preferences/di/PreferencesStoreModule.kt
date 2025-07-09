package com.anshtya.jetx.shared.preferences.di

import com.anshtya.jetx.shared.preferences.PreferencesStore
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PreferencesStoreModule {
    @Single
    fun providePreferencesStore(dataStoreComponent: DataStoreComponent): PreferencesStore =
        PreferencesStore(dataStore = dataStoreComponent.provideDataStore())
}