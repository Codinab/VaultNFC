package com.example.vaultnfc.ui

import PasswordGeneratorScreen
import android.content.Context
import com.example.vaultnfc.ui.screens.AddPasswordScreen
import com.example.vaultnfc.ui.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vaultnfc.ui.screens.*
import com.example.vaultnfc.ui.screens.PasswordsListScreen
import com.example.vaultnfc.ui.screens.SettingsScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    val startDestination = if (isLoggedIn(context)) {
        Screen.Login.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AddPassword.route) { AddPasswordScreen(navController) }
        composable(Screen.PasswordsList.route) { PasswordsListScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.PasswordGenerator.route) { PasswordGeneratorScreen() }
    }
}

fun isLoggedIn(context: Context): Boolean {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    return account != null
}


