package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.LogoutTimerPreference
import com.example.vaultnfc.data.MasterKeyTimerPreference
import com.example.vaultnfc.data.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user settings, including theme and logout timer preferences.
 *
 * @property application The application context used for initializing preferences.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _darkThemeEnabled = MutableStateFlow(true)
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

    /**
     * A list of available logout timer options.
     */
    companion object {
        val TIMEOUT_MODE = listOf("Never", "Closing app", "15 minutes", "1 day", "1 week")
    }
}
