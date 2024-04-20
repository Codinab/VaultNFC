package com.example.vaultnfc.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Settings : Screen("settings_screen")
    data object AddPassword : Screen("add_password_screen")
    data object PasswordsList : Screen("passwords_list_screen")
    data object Login : Screen("login_screen")
    data object ForgotPassword : Screen("forgot_password_screen")
    data object Register : Screen("register_screen")
    data object PasswordGenerator : Screen("password_generator_screen")
    data object EditPassword : Screen("edit_password_screen")
    data object PasswordDetails : Screen("password_details_screen")
    data object Opening : Screen("opening_screen")
    data object BluetoothClient : Screen("bluetooth_client_screen")
    data object BluetoothServer : Screen("bluetooth_server_screen")
}

