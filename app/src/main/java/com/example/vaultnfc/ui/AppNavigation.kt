package com.example.vaultnfc.ui


import PasswordGeneratorScreen
import android.content.Context
import com.example.vaultnfc.ui.screens.home.passwordview.AddPasswordScreen
import com.example.vaultnfc.ui.screens.home.PasswordsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vaultnfc.ui.screens.*
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordsListScreen
import com.example.vaultnfc.ui.screens.LoginScreen
import com.example.vaultnfc.ui.screens.SettingsScreen
import com.example.vaultnfc.ui.screens.home.passwordview.EditPasswordScreen
import com.example.vaultnfc.ui.screens.home.passwordview.PasswordDetailsScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    val startDestination = if (isLoggedIn(context)) {
        Screen.Login.route
    } else {
        Screen.Login.route
    }

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

    }

}

fun isLoggedIn(context: Context): Boolean {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    return account != null
}


