package com.anshtya.jetx

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.anshtya.jetx.notifications.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JetXApplication : Application(), Configuration.Provider, SingletonImageLoader.Factory {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.getInstance().onCreate(this)
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}