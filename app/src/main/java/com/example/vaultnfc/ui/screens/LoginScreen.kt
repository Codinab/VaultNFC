package com.example.vaultnfc.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.vaultnfc.ui.Screen

@Composable
fun LoginScreen(navController: NavController) {
    // Layout for login, e.g., a button to sign in with Google
    Button(onClick = {
        // Trigger the sign-in flow here
        // On successful sign-in, navigate to the home screen
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Login.route) {
                inclusive = true // Remove the login screen from the back stack
            }
        }
    }) {
        Text("Sign in with Google")
    }
}
