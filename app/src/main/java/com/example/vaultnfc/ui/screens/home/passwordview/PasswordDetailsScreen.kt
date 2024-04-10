package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.data.repository.PasswordSelected.passwordItemSelected
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.theme.LightRed
import com.example.vaultnfc.ui.theme.RedEnd

@Composable
fun PasswordDetailsScreen(navController: NavController) {

    val password = passwordItemSelected

    var isPasswordVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val passwordsViewModel: PasswordsViewModel = viewModel()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(RedEnd),
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp,10.dp)
                .width(80.dp)
                .height(45.dp)
                .width(IntrinsicSize.Max) // Ensure same width for both buttons // Ensure same height for both buttons
                .shadow(4.dp, RoundedCornerShape(1.dp)),
            shape = RoundedCornerShape(1.dp)

        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = LightRed),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = password.title, // Changed from Title to Username
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold
                    )

                }
                Spacer(Modifier.height(10.dp))
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
                        Toast.makeText(context, "Username copied!", Toast.LENGTH_SHORT)
                            .show() // Changed toast message
                    }) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy Username"
                        ) // Changed content description
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isPasswordVisible) {
                        Text(
                            text = "Password: ${
                                passwordsViewModel.decryptPassword(
                                    password.encryptedPassword,
                                    "Test"
                                )
                            }",
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
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "URI: ${password.uri}", // Changed from Title to Username
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(password.username)) // Copying username instead of title
                        Toast.makeText(context, "URI copied!", Toast.LENGTH_SHORT)
                            .show() // Changed toast message
                    }) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy URI"
                        ) // Changed content description
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${password.notes}", // Changed from Title to Username
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                }
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(RedEnd),
                        onClick = { navController.navigate(Screen.BluetoothClient.route) },
                        modifier = Modifier
                            .width(200.dp)
                            .height(45.dp)
                            .width(IntrinsicSize.Max) // Ensure same width for both buttons // Ensure same height for both buttons
                            .shadow(3.dp, RoundedCornerShape(1.dp)),
                        shape = RoundedCornerShape(1.dp)
                    ) {
                        Text("Share with Bluetooth")
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(RedEnd),
                        onClick = {
                            passwordsViewModel.removePassword(password)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(45.dp)
                            .width(IntrinsicSize.Max) // Ensure same width for both buttons // Ensure same height for both buttons
                            .shadow(3.dp, RoundedCornerShape(1.dp)),
                        shape = RoundedCornerShape(1.dp)
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
        }
    }
