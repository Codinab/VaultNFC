package com.example.vaultnfc.ui.screens

import PasswordsViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddPasswordScreen(navController: NavController, passwordsViewModel: PasswordsViewModel = viewModel()) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uri by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        TextField(
            value = uri,
            onValueChange = { uri = it },
            label = { Text("Uri") }
        )
        TextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") }
        )
        Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigateUp() }) {
                Text("Back")
            }
            Button(onClick = {
                if (title.isNotEmpty() && password.isNotEmpty()) {
                    passwordsViewModel.addPassword(title, username, password, uri, notes).also {
                        Toast.makeText(context, "Password added successfully", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                } else {
                    Toast.makeText(context, "Title and password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add")
            }
        }
    }
}