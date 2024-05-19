package com.example.vaultnfc.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get a reference to the DataStore with a specified name.
private const val DATA_USAGE_PREFERENCES = "data_usage_preferences"
val Context.dataUsageDataStore by preferencesDataStore(name = DATA_USAGE_PREFERENCES)

/**
 * Class responsible for managing the data usage preference in the application.
 * Utilizes DataStore for persisting the data usage preference.
 *
 * @param application The application context used to access the DataStore.
 */
class DataUsagePreference(application: Application) {
    private val dataStore = application.dataUsageDataStore

    companion object {
        private val USE_MOBILE_DATA_KEY = booleanPreferencesKey("use_mobile_data")
    }

    // Flow representing the current state of the data usage preference.
    val useMobileData: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[USE_MOBILE_DATA_KEY] ?: true // Default to true if no preference is set.
    }

    /**
     * Sets the data usage preference in the DataStore.
     *
     * @param isEnabled Whether the app should use mobile data.
     */
    suspend fun setUseMobileData(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_MOBILE_DATA_KEY] = isEnabled
        }
    }
}
