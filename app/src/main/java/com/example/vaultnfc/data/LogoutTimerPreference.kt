package com.example.vaultnfc.data

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get a reference to the DataStore with a specified name.
private const val LOGOUT_TIMER_PREFERENCES = "logout_timer_preferences"
val Context.logoutTimerDataStore by preferencesDataStore(name = LOGOUT_TIMER_PREFERENCES)

/**
 * Class responsible for managing the logout timer preference in the application.
 * Utilizes DataStore for persisting the logout timer preference.
 *
 * @param application The application context used to access the DataStore.
 */
class LogoutTimerPreference(application: Application) {
    private val dataStore = application.logoutTimerDataStore

    companion object {
        private val LOGOUT_TIMER_KEY = stringPreferencesKey("logout_timer_option")
    }

    // Flow representing the current state of the logout timer preference.
    val logoutTimerOption: Flow<String> = dataStore.data.map { preferences ->
        preferences[LOGOUT_TIMER_KEY] ?: SettingsViewModel.LOGIN_TIMEOUT_MODE[1]
    }

    /**
     * Sets the logout timer preference in the DataStore.
     *
     * @param option The logout timer option to be set.
     */
    suspend fun setLogoutTimerOption(option: String) {
        dataStore.edit { preferences ->
            preferences[LOGOUT_TIMER_KEY] = option
        }
    }
}

