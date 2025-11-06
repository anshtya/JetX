package com.anshtya.jetx.core.coroutine

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ExternalScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoScope