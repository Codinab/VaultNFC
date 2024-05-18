package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.data.repository.PasswordSelected.passwordItemSelected
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.components.BackButton
import com.example.vaultnfc.ui.theme.LightRed
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.viewmodel.MasterKeyViewModel

@Composable
fun PasswordDetailsScreen(navController: NavController) {

    val password = passwordItemSelected
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val passwordsViewModel: PasswordsViewModel = viewModel()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        passwordDetailsCard(password, passwordsViewModel, clipboardManager, context, navController)
    }
    BackButton(navController)
}

@Composable
fun passwordDetailsCard(
    password: PasswordItem,  // Assuming Password is a data class
    passwordsViewModel: PasswordsViewModel,
    clipboardManager: ClipboardManager,
    context: Context,
    navController: NavController,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = LightRed),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            passwordInfo("Username", password.username, clipboardManager, context)
            passwordVisibilityRow(
                password,
                isPasswordVisible,
                passwordsViewModel,
                clipboardManager,
                context
            ) { isPasswordVisible = !isPasswordVisible }
            passwordInfo("URI", password.uri, clipboardManager, context)
            passwordInfo("Notes", password.notes, clipboardManager, context)
            actionButtons(navController, password, passwordsViewModel)
        }
    }
}

@Composable
fun passwordInfo(
    label: String,
    value: String,
    clipboardManager: ClipboardManager,
    context: Context,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            clipboardManager.setText(AnnotatedString(value))
            Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
        }) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy $label")
        }
    }
    Spacer(Modifier.height(14.dp))
}

@Composable
fun passwordVisibilityRow(
    password: PasswordItem,
    isPasswordVisible: Boolean,
    passwordsViewModel: PasswordsViewModel,
    clipboardManager: ClipboardManager,
    context: Context,
    onVisibilityChange: () -> Unit,  // Lambda function to toggle visibility
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val masterKeyViewModel: MasterKeyViewModel = viewModel()
        if (masterKeyViewModel.getMasterKey() == null) {

            Text(
                text = "Master Key not set",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            return
        }
        if (isPasswordVisible) {
            Text(
                text = "Password: ${
                    passwordsViewModel.decryptPassword(
                        password.encryptedPassword,
                        masterKeyViewModel.getMasterKey()!!
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
        IconButton(onClick = onVisibilityChange) {  // Use the lambda function to toggle visibility
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
}


@Composable
fun actionButtons(
    navController: NavController,
    password: PasswordItem,
    passwordsViewModel: PasswordsViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(RedEnd),
            onClick = { navController.navigate(Screen.BluetoothClient.route) },
            modifier = Modifier
                .size(width = 200.dp, height = 45.dp),
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
                .size(width = 100.dp, height = 45.dp),
            shape = RoundedCornerShape(1.dp)
        ) {
            Text("Remove")
        }
    }
}
