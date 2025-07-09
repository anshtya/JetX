package com.anshtya.jetx.shared.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.database.JetXDatabase
import org.koin.core.annotation.Single

@Single
actual class DatabaseBuilderComponent actual constructor(val ctx: ContextWrapper) {
    @Single
    actual fun provideDatabaseBuilder(): RoomDatabase.Builder<JetXDatabase> {
        val appContext = ctx.context.applicationContext
        val dbFile = appContext.getDatabasePath("my_room.db")
        return Room.databaseBuilder<JetXDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}