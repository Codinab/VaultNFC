package com.example.vaultnfc.ui


import PasswordGeneratorScreen
import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vaultnfc.ui.screens.BluetoothScreen
import com.example.vaultnfc.ui.screens.SettingsScreen
import com.example.vaultnfc.ui.screens.home.PasswordsScreen
import com.example.vaultnfc.ui.screens.home.passwordview.AddPasswordScreen
import com.example.vaultnfc.ui.screens.home.passwordview.EditPasswordScreen
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordDetailsScreen
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordsListScreen
import com.example.vaultnfc.ui.screens.starting.LoginScreen
import com.example.vaultnfc.ui.screens.starting.OpeningScreen
import com.example.vaultnfc.ui.screens.starting.RegisterScreen
import com.example.vaultnfc.ui.viewmodel.BluetoothViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun AppNavigation(application: Application) {
    val navController = rememberNavController()

    val bluetoothViewModel = BluetoothViewModel(application)

    NavHost(navController = navController, startDestination = Screen.Opening.route) {
        composable(Screen.Home.route) { PasswordsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AddPassword.route) { AddPasswordScreen(navController) }
        composable(Screen.PasswordsList.route) { PasswordsListScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.PasswordGenerator.route) { PasswordGeneratorScreen(navController) }
        composable(Screen.EditPassword.route) { EditPasswordScreen(navController) }
        composable(Screen.PasswordDetails.route) { PasswordDetailsScreen(navController) }
        // Assuming NFCSharePopupScreen and NFCReceivePopupScreen are dialogues or popups and may not require a composable route.
        composable(Screen.PasswordGenerator.route) { PasswordGeneratorScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Opening.route) { OpeningScreen(navController) }
        composable(Screen.Bluetooth.route) { BluetoothScreen(application, bluetoothViewModel) }

    }

}

fun isLoggedIn(context: Context): Boolean {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    return account != null
}


