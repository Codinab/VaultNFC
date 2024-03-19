package com.example.vaultnfc.ui

import AddPasswordScreen
import HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vaultnfc.ui.screens.PasswordsListScreen
import com.example.vaultnfc.ui.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AddPassword.route) { AddPasswordScreen(navController) }
        composable(Screen.PasswordsList.route) { PasswordsListScreen(navController) }
    }
}

