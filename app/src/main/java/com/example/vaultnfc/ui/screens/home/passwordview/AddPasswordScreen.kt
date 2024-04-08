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
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.theme.RedEnd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(navController: NavController, passwordsViewModel: PasswordsViewModel = viewModel()) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uri by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    passwordsViewModel.fetch()

    var selectedFolder by remember { mutableStateOf<String?>(null) }
    val folders by passwordsViewModel.foldersList.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label and field for Title
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {title = it},
                label = {Text(text = "Enter the Title", color = Color.Black)},
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Title, contentDescription ="Set the title")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                    )

            )
        }
        // Label and field for Username
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {username = it},
                label = {Text(text = "Enter the username", color = Color.Black)},
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription ="Set the title")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                )

            )
        }
        // Label and field for Password
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = {Text(text = "Enter the Password", color = Color.Black)},
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Password, contentDescription ="Set the title")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                )

            )
        }
        // Label and field for URI
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uri,
                onValueChange = {uri = it},
                label = {Text(text = "Enter the URI", color = Color.Black)},
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.FindInPage, contentDescription ="Set the title")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                )

            )
        }
        // Label and field for Notes
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = notes,
                onValueChange = {notes = it},
                label = {Text(text = "Enter the Notes", color = Color.Black)},
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.NoteAlt, contentDescription ="Set the title")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                )

            )
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = RedEnd,
                ),
                value = selectedFolder ?: "No Folder",
                onValueChange = {},
                label = { Text("Folder", color = Color.Black) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropdownMenuItem(
                    text = { Text("No Folder") },
                    onClick = {
                        selectedFolder = null
                        expanded = false
                    }
                )
                folders.forEach { folder ->
                    DropdownMenuItem(
                        text = { Text(folder.name) },
                        onClick = {
                            selectedFolder = folder.name
                            expanded = false
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(RedEnd),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .heightIn(min = 36.dp)
                    .shadow(18.dp, RoundedCornerShape(1.dp)),
                shape = RoundedCornerShape(1.dp)
            ) {
                Text("Back", color = Color.White)
            }
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        passwordsViewModel.addPassword(title, username, password, uri, notes).also {
                            Toast.makeText(context, "Password added successfully", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    } else {
                        Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(RedEnd),
                modifier = Modifier
                    .heightIn(min = 36.dp)
                    .shadow(18.dp, RoundedCornerShape(1.dp)),
                shape = RoundedCornerShape(1.dp)
            ) {
                Text("Add", color = Color.White)
            }
        }
    }
}