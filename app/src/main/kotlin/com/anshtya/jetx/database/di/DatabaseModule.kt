package com.anshtya.jetx.database.di

import android.content.Context
import androidx.room.Room
import com.anshtya.jetx.database.JetXDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideJetXDatabase(@ApplicationContext context: Context): JetXDatabase {
        return Room.databaseBuilder(
            context,
            JetXDatabase::class.java,
            "jetx-database"
        ).build()
    }
}