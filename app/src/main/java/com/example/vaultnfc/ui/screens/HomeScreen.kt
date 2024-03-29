package com.example.vaultnfc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vaultnfc.ui.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Add a button to navigate to the AddPassword screen
        Button(onClick = { navController.navigate(Screen.AddPassword.route) }) {
            Text("Add New Password")

        }

        Button(onClick = { navController.navigate(Screen.PasswordsList.route) }) {
            Text("View Stored Passwords")
        }

        Button(onClick = { navController.navigate(Screen.PasswordGenerator.route) }) {
            Text("Generate a Password")
        }


    }
}
