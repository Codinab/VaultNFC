package com.example.vaultnfc.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.vaultnfc.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val NOTIFICATION_PREFERENCES = "notification_preferences"
val Context.notificationDataStore by preferencesDataStore(name = NOTIFICATION_PREFERENCES)

class NotificationPreference(application: Application) {
    private val dataStore = application.notificationDataStore

    companion object {
        private val PASSWORD_CREATION_NOTIFICATION_KEY = booleanPreferencesKey("password_creation_notification")
        private val PASSWORD_UPDATE_NOTIFICATION_KEY = booleanPreferencesKey("password_update_notification")
        private val PASSWORD_DELETION_NOTIFICATION_KEY = booleanPreferencesKey("password_deletion_notification")
    }

    val passwordCreationNotification: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PASSWORD_CREATION_NOTIFICATION_KEY] ?: true
    }

    val passwordUpdateNotification: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PASSWORD_UPDATE_NOTIFICATION_KEY] ?: true
    }

    val passwordDeletionNotification: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PASSWORD_DELETION_NOTIFICATION_KEY] ?: true
    }

    suspend fun setPasswordCreationNotification(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PASSWORD_CREATION_NOTIFICATION_KEY] = isEnabled
        }
    }

    suspend fun setPasswordUpdateNotification(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PASSWORD_UPDATE_NOTIFICATION_KEY] = isEnabled
        }
    }

    suspend fun setPasswordDeletionNotification(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PASSWORD_DELETION_NOTIFICATION_KEY] = isEnabled
        }
    }

    fun isChannelEnabled(context: Context, channelId: String): Boolean {
        return runBlocking {
            val enabled = when (channelId) {
                context.getString(R.string.password_creation_channel_id) -> {
                    passwordCreationNotification.first()
                }
                context.getString(R.string.password_update_channel_id) -> {
                    passwordUpdateNotification.first()
                }
                context.getString(R.string.password_deletion_channel_id) -> {
                    passwordDeletionNotification.first()
                }
                else -> {
                    true // Default to true if the channel ID is not recognized
                }
            }
            enabled
        }
    }

}
