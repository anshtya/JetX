package com.anshtya.jetx.data.network.interceptor

import com.anshtya.jetx.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

val httpLoggingInterceptor = HttpLoggingInterceptor()
    .apply {
        if (BuildConfig.DEBUG) {
            level = Level.BODY
        }
    }