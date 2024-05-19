package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FindInPage
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.components.BackgroundImageWrapper

private const val s = "Password added successfully"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    navController: NavController,
    passwordsViewModel: PasswordsViewModel = viewModel()
) {
    BackgroundImageWrapper {


        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var uri by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var tag by remember { mutableStateOf("") }
        passwordsViewModel.fetch()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textFieldModifier = Modifier.padding(vertical = 16.dp)
            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            )

            val textField =
                @Composable { label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector ->
                    Column(
                        modifier = textFieldModifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = onValueChange,
                            label = { Text(text = label, color = MaterialTheme.colorScheme.tertiary) },
                            leadingIcon = { Icon(imageVector = icon, contentDescription = label) },
                            colors = textFieldColors,
                        )
                    }
                }

            textField(
                stringResource(R.string.enter_the_title),
                title,
                { title = it },
                Icons.Outlined.Title
            )
            textField(
                stringResource(R.string.enter_the_username),
                username,
                { username = it },
                Icons.Outlined.Person
            )
            textField(
                stringResource(R.string.enter_the_password),
                password,
                { password = it },
                Icons.Outlined.Password
            )
            textField(
                stringResource(R.string.enter_the_uri),
                uri,
                { uri = it },
                Icons.Outlined.FindInPage
            )
            textField(
                stringResource(R.string.enter_the_notes),
                notes,
                { notes = it },
                Icons.Outlined.NoteAlt
            )
            textField(stringResource(R.string.enter_tag), tag, { tag = it }, Icons.Outlined.Tag)


            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val buttonModifier = Modifier
                    .heightIn(min = 36.dp)
                    .shadow(18.dp, RoundedCornerShape(1.dp))

                Button(
                    onClick = { navController.navigateUp() },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = buttonModifier.padding(end = 16.dp),
                    shape = RoundedCornerShape(1.dp)
                ) { Text("Back", color = MaterialTheme.colorScheme.secondary) }

                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            passwordsViewModel.addPassword(
                                title,
                                username,
                                password,
                                uri,
                                notes,
                                tag
                            ).also {
                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
                                passwordsViewModel.fetch()
                            }
                        } else {
                            Toast.makeText(context,
                                context.getString(R.string.title_cannot_be_empty), Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(1.dp)
                ) { Text("Add", color = MaterialTheme.colorScheme.secondary) }
            }
        }
    }
}
