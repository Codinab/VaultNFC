package com.example.vaultnfc.ui.screens

import PasswordsViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.model.PasswordItem


@Composable
fun PasswordsListScreen(
    navController: NavController,
    passwordsViewModel: PasswordsViewModel = viewModel(),
) {
    val passwordsList by passwordsViewModel.passwordsList.observeAsState(emptyList())



    Column {
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }

        PasswordsListView(
            passwordsList = passwordsList,
            onRemove = passwordsViewModel::removePassword
        )
    }
}


@Composable
fun PasswordsListView(passwordsList: List<PasswordItem>, onRemove: (PasswordItem) -> Unit) {
    LazyColumn {
        items(passwordsList) { password ->
            PasswordItemView(password = password, onRemove = onRemove)
        }
    }
}


@Composable
fun PasswordItemView(password: PasswordItem, onRemove: (PasswordItem) -> Unit) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val passwordsViewModel: PasswordsViewModel = viewModel()

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Username: ${password.username}", // Changed from Title to Username
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(password.username)) // Copying username instead of title
                    Toast.makeText(context, "Username copied!", Toast.LENGTH_SHORT).show() // Changed toast message
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Username") // Changed content description
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isPasswordVisible) {
                    Text(
                        text = "Password: ${
                            passwordsViewModel.decryptPassword(password.encryptedPassword, "Test")}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = "Password: ******",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(password.encryptedPassword))
                    Toast.makeText(context, "Password copied!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Password")
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        onRemove(password)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Remove")
                }
            }
        }
    }
}