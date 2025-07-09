package com.anshtya.jetx.shared

import com.anshtya.jetx.shared.coroutine.di.CoroutineDispatcherModule
import com.anshtya.jetx.shared.coroutine.di.CoroutineScopeModule
import com.anshtya.jetx.shared.preferences.di.PreferencesStoreModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        CoroutineDispatcherModule::class,
        CoroutineScopeModule::class,
        PreferencesStoreModule::class
    ]
)
@ComponentScan
class SharedModule