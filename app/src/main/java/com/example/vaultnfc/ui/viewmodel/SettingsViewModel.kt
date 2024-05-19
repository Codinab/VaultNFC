package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.SyncManager
import com.example.vaultnfc.data.preferences.DataUsagePreference
import com.example.vaultnfc.data.preferences.LogoutTimerPreference
import com.example.vaultnfc.data.preferences.MasterKeyTimerPreference
import com.example.vaultnfc.data.preferences.NotificationPreference
import com.example.vaultnfc.data.preferences.SyncPreference
import com.example.vaultnfc.data.preferences.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user settings, including theme and logout timer preferences.
 *
 * @property application The application context used for initializing preferences.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val syncManager = SyncManager(application)


    private val themePreference = ThemePreference(application)

    /**
     * A Flow that emits the current theme preference (dark or light theme).
     */
    val darkThemeEnabled: Flow<Boolean> = themePreference.darkThemeEnabled

    /**
     * Toggles the dark theme preference.
     *
     * @param isEnabled Whether the dark theme should be enabled or not.
     */
    fun toggleDarkTheme(isEnabled: Boolean) = viewModelScope.launch {
        themePreference.toggleDarkTheme(isEnabled)
    }

    private val logoutTimerPreference = LogoutTimerPreference(application)

    /**
     * A Flow that emits the current logout timer option.
     */
    val logoutTimerOption: Flow<String> = logoutTimerPreference.logoutTimerOption

    /**
     * Sets the logout timer option.
     *
     * @param option The selected logout timer option.
     */
    fun setLogoutTimerOption(option: String) = viewModelScope.launch {
        logoutTimerPreference.setLogoutTimerOption(option)
    }

    private val masterKeyTimerPreference = MasterKeyTimerPreference(application)

    /**
     * A Flow that emits the current MasterKey timer option.
     */
    val masterKeyTimerOption: Flow<String> = masterKeyTimerPreference.masterKeyTimerOption

    /**
     * Sets the MasterKey timer option.
     *
     * @param option The selected MasterKey timer option.
     */
    fun setMasterKeyTimerOption(option: String) = viewModelScope.launch {
        masterKeyTimerPreference.setMasterKeyTimerOption(option)
    }

    private val dataUsagePreference = DataUsagePreference(application)

    /**
     * A Flow that emits the current data usage preference (use mobile data or Wi-Fi only).
     */
    val useMobileData: Flow<Boolean> = dataUsagePreference.useMobileData

    /**
     * Sets the data usage preference.
     *
     * @param isEnabled Whether the app should use mobile data.
     */
    fun setUseMobileData(isEnabled: Boolean) = viewModelScope.launch {
        dataUsagePreference.setUseMobileData(isEnabled)
        syncManager.updateSyncSettings()
    }

    private val syncPreference = SyncPreference(application)

    /**
     * A Flow that emits the current synchronization preference (sync with cloud or local storage).
     */
    val syncWithCloud: Flow<Boolean> = syncPreference.syncWithCloud

    /**
     * Sets the synchronization preference.
     *
     * @param isEnabled Whether the app should synchronize with the cloud.
     */
    fun setSyncWithCloud(isEnabled: Boolean) = viewModelScope.launch {
        syncPreference.setSyncWithCloud(isEnabled)
        syncManager.updateSyncSettings()
    }

    private val notificationPreference = NotificationPreference(application)

    /**
     * Flows that emit the current notification preferences.
     */
    val passwordCreationNotification: Flow<Boolean> = notificationPreference.passwordCreationNotification
    val passwordUpdateNotification: Flow<Boolean> = notificationPreference.passwordUpdateNotification
    val passwordDeletionNotification: Flow<Boolean> = notificationPreference.passwordDeletionNotification

    /**
     * Sets the notification preferences.
     *
     * @param isEnabled Whether the notification should be enabled or not.
     */
    fun setPasswordCreationNotification(isEnabled: Boolean) = viewModelScope.launch {
        notificationPreference.setPasswordCreationNotification(isEnabled)
    }

    /**
     * Sets the notification preferences.
     *
     * @param isEnabled Whether the notification should be enabled or not.
     */
    fun setPasswordUpdateNotification(isEnabled: Boolean) = viewModelScope.launch {
        notificationPreference.setPasswordUpdateNotification(isEnabled)
    }

    /**
     * Sets the notification preferences.
     *
     * @param isEnabled Whether the notification should be enabled or not.
     */
    fun setPasswordDeletionNotification(isEnabled: Boolean) = viewModelScope.launch {
        notificationPreference.setPasswordDeletionNotification(isEnabled)
    }

    /**
     * A list of available logout timer options.
     */
    companion object {
        val TIMEOUT_MODE = listOf("Never", "Closing app", "15 minutes", "1 day", "1 week")
    }
}
