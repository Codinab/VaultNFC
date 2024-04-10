package com.example.vaultnfc.data

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.logoutTimerDataStore by preferencesDataStore(name = "logout_timer_preferences")

class LogoutTimerPreference(application: Application) {
    private val dataStore = application.logoutTimerDataStore

    companion object {
        private val LOGOUT_TIMER_KEY = stringPreferencesKey("logout_timer_option")
    }

    val logoutTimerOption: Flow<String> = dataStore.data.map { preferences ->
        preferences[LOGOUT_TIMER_KEY] ?: "Closing app"
    }

    suspend fun setLogoutTimerOption(option: String) {
        dataStore.edit { preferences ->
            preferences[LOGOUT_TIMER_KEY] = option
        }
    }
}

