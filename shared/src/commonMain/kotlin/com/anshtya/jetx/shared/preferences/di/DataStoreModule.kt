package com.anshtya.jetx.shared.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anshtya.jetx.shared.ContextWrapper
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class DataStoreModule

@Single
expect class DataStoreComponent(ctx: ContextWrapper) {
    @Single
    fun provideDataStore(): DataStore<Preferences>
}