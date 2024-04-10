package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreference = ThemePreference(application)

    val darkThemeEnabled: Flow<Boolean> = themePreference.darkThemeEnabled

    fun toggleDarkTheme(isEnabled: Boolean) = viewModelScope.launch {
        themePreference.toggleDarkTheme(isEnabled)
    }
}
