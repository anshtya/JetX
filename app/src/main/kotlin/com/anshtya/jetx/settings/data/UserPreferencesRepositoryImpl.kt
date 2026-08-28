package com.anshtya.jetx.settings.data

import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.core.preferences.model.ThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val store: JetxPreferencesStore
) : UserPreferencesRepository {
    override val theme: Flow<ThemeOption> = store.user.appUiProperties.map { it.theme }

    override suspend fun setTheme(theme: ThemeOption) {
        store.user.setTheme(theme.name)
    }
}
