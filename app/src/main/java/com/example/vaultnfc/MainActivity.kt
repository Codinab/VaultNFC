package com.example.vaultnfc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.vaultnfc.ui.AppNavigation
import com.example.vaultnfc.ui.theme.VaultNFCTheme
import com.example.vaultnfc.ui.viewmodel.LoginViewModel
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel
import com.example.vaultnfc.ui.viewmodel.SignInViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModelProvider: SignInViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var settingsViewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelProvider = ViewModelProvider(this)[SignInViewModel::class.java]
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        settingsViewModel.logoutTimerOption.asLiveData().observe(this) { option ->
            currentLogoutOption = option
        }

        setContent {
            VaultNFCTheme {
                AppNavigation(this.application)
            }
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    private var currentLogoutOption = "Closing app"
    override fun onStop() {
        super.onStop()

            if (currentLogoutOption == "Closing app") {
                loginViewModel.logout(this)
            }
    }


    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>



}