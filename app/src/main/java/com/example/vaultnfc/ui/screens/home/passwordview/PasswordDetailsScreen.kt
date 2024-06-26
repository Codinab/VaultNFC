package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import android.app.Application
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.data.repository.PasswordSelected.passwordItemSelected
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.components.BackgroundImageWrapper
import com.example.vaultnfc.ui.viewmodel.MasterKeyViewModel

@Composable
fun PasswordDetailsScreen(navController: NavController, application: Application) {
    val password = passwordItemSelected
    var isPasswordVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val passwordsViewModel = PasswordsViewModel(application)

    BackgroundImageWrapper {

        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .size(80.dp, 45.dp)
                    .shadow(4.dp, RoundedCornerShape(1.dp)),
                shape = RoundedCornerShape(1.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.tertiary)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = password.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Username: ${password.username}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        CopyIconButton(
                            text = password.username,
                            clipboardManager = clipboardManager,
                            context = context
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    val masterKeyViewModel: MasterKeyViewModel = viewModel()

                    if (masterKeyViewModel.isMasterKeySet.value == false) {
                        navController.navigate(Screen.Opening.route) {
                            popUpTo(Screen.Opening.route) { inclusive = true }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Password: ${
                                if (isPasswordVisible) PasswordsViewModel.decryptPassword(
                                    password.encryptedPassword,
                                    masterKeyViewModel.getMasterKey()!!
                                ) else stringResource(R.string.no_password)
                            }",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        TogglePasswordVisibilityIconButton(isPasswordVisible = isPasswordVisible) {
                            isPasswordVisible = !isPasswordVisible
                        }
                        CopyIconButton(
                            text = password.encryptedPassword,
                            clipboardManager = clipboardManager,
                            context = context
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "URI: ${password.uri}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        CopyIconButton(
                            text = password.uri,
                            clipboardManager = clipboardManager,
                            context = context
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Notes: ${password.notes}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ActionButton(
                            text = stringResource(R.string.share_with_bluetooth),
                            onClick = { navController.navigate(Screen.BluetoothClient.route) },
                            modifier = Modifier.size(200.dp, 45.dp)
                        )

                        ActionButton(
                            text = stringResource(R.string.remove),
                            onClick = {
                                passwordsViewModel.removePassword(password,)
                                navController.popBackStack()
                            },
                            modifier = Modifier.size(100.dp, 45.dp)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CopyIconButton(text: String, clipboardManager: ClipboardManager, context: Context) {
    IconButton(onClick = {
        clipboardManager.setText(AnnotatedString(text))
        Toast.makeText(context, "${text.capitalize()} copied!", Toast.LENGTH_SHORT).show()
    }) {
        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy ${text.capitalize()}", tint = MaterialTheme.colorScheme.tertiary,)
    }
}

@Composable
fun TogglePasswordVisibilityIconButton(isPasswordVisible: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                R.string.show_password
            )
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        onClick = onClick,
        modifier = modifier.shadow(3.dp, RoundedCornerShape(1.dp)),
        shape = RoundedCornerShape(1.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.tertiary)
    }
}
