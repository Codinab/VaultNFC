package com.example.vaultnfc.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get a reference to the DataStore with a specified name.
private const val SYNC_PREFERENCES = "sync_preferences"
val Context.syncDataStore by preferencesDataStore(name = SYNC_PREFERENCES)

/**
 * Class responsible for storing the synchronization preference of the application.
 * Utilizes DataStore for persisting the synchronization preference.
 *
 * @param application The application context used to access the DataStore.
 */
class SyncPreference(application: Application) {
    private val dataStore = application.syncDataStore

    companion object {
        private val SYNC_WITH_CLOUD_KEY = booleanPreferencesKey("sync_with_cloud")
    }

    // Flow representing the current state of the synchronization preference.
    val syncWithCloud: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SYNC_WITH_CLOUD_KEY] ?: true // Default to true if no preference is set.
    }

    /**
     * Sets the synchronization preference in the DataStore.
     *
     * @param isEnabled Whether the app should synchronize with the cloud.
     */
    suspend fun setSyncWithCloud(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SYNC_WITH_CLOUD_KEY] = isEnabled
        }
    }
}
