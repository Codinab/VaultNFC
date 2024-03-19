package com.example.vaultnfc.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Settings : Screen("settings_screen")

}
