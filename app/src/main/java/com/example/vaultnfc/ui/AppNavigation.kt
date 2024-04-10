package com.example.vaultnfc.ui


import PasswordGeneratorScreen
import android.app.Application
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vaultnfc.ui.screens.BluetoothClientScreen
import com.example.vaultnfc.ui.screens.BluetoothServerScreen
import com.example.vaultnfc.ui.screens.SettingsScreen
import com.example.vaultnfc.ui.screens.home.PasswordsScreen
import com.example.vaultnfc.ui.screens.home.passwordview.AddPasswordScreen
import com.example.vaultnfc.ui.screens.home.passwordview.EditPasswordScreen
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordDetailsScreen
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordsListScreen
import com.example.vaultnfc.ui.screens.starting.LoginScreen
import com.example.vaultnfc.ui.screens.starting.OpeningScreen
import com.example.vaultnfc.ui.screens.starting.RegisterScreen

@Composable
fun AppNavigation(application: Application) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Screen.Opening.route) {
        // Starting
        composable(Screen.Home.route) { PasswordsScreen(navController, application) }
        composable(Screen.Settings.route) { SettingsScreen(navController, application) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Opening.route) { OpeningScreen(navController) }

        // Old
        composable(Screen.PasswordsList.route) { PasswordsListScreen(navController) }

        composable(Screen.AddPassword.route) { AddPasswordScreen(navController) }
        composable(Screen.PasswordGenerator.route) { PasswordGeneratorScreen(navController) }
        composable(Screen.EditPassword.route) { EditPasswordScreen(navController) }
        composable(Screen.PasswordDetails.route) { PasswordDetailsScreen(navController) }

        //Bluetooth
        //composable(Screen.Bluetooth.route) { BluetoothScreen(application, bluetoothViewModel) }
        composable(Screen.BluetoothClient.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BluetoothClientScreen(application, navController)
            } else {
                navController.popBackStack()
            }
        }
        composable(Screen.BluetoothServer.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BluetoothServerScreen(application, navController)
            } else {
                navController.popBackStack()
            }
        }
    }

}


