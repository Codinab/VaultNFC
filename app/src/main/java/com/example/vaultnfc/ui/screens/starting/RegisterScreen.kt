package com.example.vaultnfc.ui.screens.starting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.components.BackgroundImageWrapper
import com.example.vaultnfc.ui.viewmodel.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    BackgroundImageWrapper {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        val registrationError by loginViewModel.registrationError.observeAsState()
        val context = LocalContext.current


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        stringResource(R.string.email),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Mail, contentDescription = "")
                }

            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        stringResource(R.string.password),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Password, contentDescription = "")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(
                        stringResource(R.string.confirm_password),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Password, contentDescription = "")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        loginViewModel.register(email, password) {
                            navController.navigate(Screen.Login.route)
                        }
                    } else {
                        loginViewModel.registrationError.postValue(context.getString(R.string.passwords_do_not_match))
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp)
                    .width(IntrinsicSize.Max)
                    .shadow(3.dp, RoundedCornerShape(1.dp)),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(1.dp),

                ) {
                Text(stringResource(R.string.register), color = MaterialTheme.colorScheme.tertiary)
            }

            // Handle registration errors
            registrationError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Navigation to login screen
            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text(stringResource(R.string.already_have_an_account_login), color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}
