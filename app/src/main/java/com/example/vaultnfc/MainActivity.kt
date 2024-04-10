package com.example.vaultnfc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.vaultnfc.ui.AppNavigation
import com.example.vaultnfc.ui.theme.VaultNFCTheme
import com.example.vaultnfc.ui.viewmodel.LoginViewModel
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel

/**
 * The main activity for the application, serving as the entry point.
 *
 * This activity is responsible for initializing the application's theme, navigation, and view models.
 * It observes the selected logout option from the settings and handles the logout process based on user preferences.
 */
class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    /**
     * Initializes the activity, setting up view models, theme, and app navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModel initialization
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // Observing logout timer option changes
        settingsViewModel.logoutTimerOption.asLiveData().observe(this) { option ->
            currentLogoutOption = option
        }

        // Setting content view with Compose UI
        setContent {
            VaultNFCTheme {
                AppNavigation(application)
            }
        }
    }

    /**
     * Called when a new intent is received.
     *
     * Use this method to handle new intents sent to the activity while it's running.
     * This can occur when the activity is already running in the foreground or if it's being re-launched
     * with a new intent after being destroyed.
     *
     * @param intent The new intent that was started for the activity.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    private var currentLogoutOption = SettingsViewModel.LOGIN_TIMEOUT_MODE[1]

    /**
     * Called when the activity is about to stop.
     *
     * It checks the current logout option and initiates the logout process if
     * the option is set to "Closing app".
     */
    override fun onStop() {
        super.onStop()

        if (currentLogoutOption == SettingsViewModel.LOGIN_TIMEOUT_MODE[1]) {
            loginViewModel.logout(this)
        }
    }
}