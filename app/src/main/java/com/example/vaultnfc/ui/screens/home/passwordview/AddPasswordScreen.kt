package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Text(
                text = "Title",
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier
                    .border(color = Color.Red, width = 1.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(300.dp)
            )
        }
        // Label and field for Username
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Username",
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            BasicTextField(
                value = username,
                onValueChange = { username = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier
                    .border(color = Color.Red, width = 1.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(300.dp)
            )
        }
        // Label and field for Password
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Password",
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier
                    .border(color = Color.Red, width = 1.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(300.dp)
            )
        }
        // Label and field for URI
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "URI",
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            BasicTextField(
                value = uri,
                onValueChange = { uri = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier
                    .border(color = Color.Red, width = 1.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(300.dp)
            )
        }
        // Label and field for Notes
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notes",
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            BasicTextField(
                value = notes,
                onValueChange = { notes = it },
                textStyle = TextStyle(color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                modifier = Modifier
                    .border(color = Color.Red, width = 1.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(300.dp)
            )
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = selectedFolder ?: "No Folder",
                onValueChange = {},
                label = { Text("Folder") },
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
                    .heightIn(min = 36.dp),
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
                    .heightIn(min = 36.dp),
                shape = RoundedCornerShape(1.dp)
            ) {
                Text("Add", color = Color.White)
            }
        }
    }
}