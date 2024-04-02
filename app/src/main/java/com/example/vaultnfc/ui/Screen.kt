package com.example.vaultnfc.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Settings : Screen("settings_screen")
    data object AddPassword : Screen("add_password_screen")
    data object PasswordsList : Screen("passwords_list_screen")
    data object Login : Screen("login_screen")
    data object PasswordGenerator : Screen("password_generator_screen")


}
