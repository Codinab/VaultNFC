package com.example.vaultnfc.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get a reference to the DataStore with a specified name.
private const val MASTER_KEY_TIMER_PREFERENCES = "master_key_timer_preferences"
val Context.masterKeyTimerDataStore by preferencesDataStore(name = MASTER_KEY_TIMER_PREFERENCES)

/**
 * Class responsible for managing the MasterKey timer preference in the application.
 * Utilizes DataStore for persisting the MasterKey timer preference.
 *
 * @param application The application context used to access the DataStore.
 */
class MasterKeyTimerPreference(application: Application) {
    private val dataStore = application.masterKeyTimerDataStore

    companion object {
        private val MASTER_KEY_TIMER_KEY = stringPreferencesKey("master_key_timer_option")
    }

    // Flow representing the current state of the MasterKey timer preference.
    val masterKeyTimerOption: Flow<String> = dataStore.data.map { preferences ->
        preferences[MASTER_KEY_TIMER_KEY] ?: SettingsViewModel.TIMEOUT_MODE[1]
    }

    /**
     * Sets the MasterKey timer preference in the DataStore.
     *
     * @param option The MasterKey timer option to be set.
     */
    suspend fun setMasterKeyTimerOption(option: String) {
        dataStore.edit { preferences ->
            preferences[MASTER_KEY_TIMER_KEY] = option
        }
    }
}
