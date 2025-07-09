package com.anshtya.jetx.shared.preferences.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.preferences.createDataStore
import com.anshtya.jetx.shared.preferences.dataStoreFileName
import org.koin.core.annotation.Single

@Single
actual class DataStoreComponent actual constructor(val ctx: ContextWrapper) {
    @Single
    actual fun provideDataStore(): DataStore<Preferences> = createDataStore(ctx.context.applicationContext)
}

private fun createDataStore(context: Context): DataStore<Preferences> {
    Log.d("foo", "creating datastore instance")
    return createDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
}