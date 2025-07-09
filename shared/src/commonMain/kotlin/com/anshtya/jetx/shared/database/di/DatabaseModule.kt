package com.anshtya.jetx.shared.database.di

import androidx.room.RoomDatabase
import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.database.JetXDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class DatabaseBuilderModule

@Single
expect class DatabaseBuilderComponent(ctx: ContextWrapper) {
    @Single
    fun provideDatabaseBuilder(): RoomDatabase.Builder<JetXDatabase>
}

@Module
class DatabaseModule {
    @Single
    fun provideDatabase(databaseBuilderComponent: DatabaseBuilderComponent): JetXDatabase {
        return databaseBuilderComponent.provideDatabaseBuilder().build()
    }
}