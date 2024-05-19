package com.example.vaultnfc.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get a reference to the DataStore with a specified name.
private const val THEME_PREFERENCES = "theme_preferences"
val Context.dataStore by preferencesDataStore(name = THEME_PREFERENCES)

/**
 * Class responsible for managing theme preferences in the application.
 * Utilizes DataStore for persisting theme preferences.
 *
 * @param application The application context used to access the DataStore.
 */
class ThemePreference(application: Application) {
    private val dataStore = application.dataStore

    companion object {
        // Constant key for storing and retrieving the dark theme preference.
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
    }

    // Flow representing the current state of the dark theme preference.
    // Emits true if dark theme is enabled, false otherwise.
    val darkThemeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    /**
     * Toggles the dark theme preference in the DataStore.
     *
     * @param isEnabled True to enable dark theme, false to disable.
     */
    suspend fun toggleDarkTheme(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isEnabled
        }
    }
}
