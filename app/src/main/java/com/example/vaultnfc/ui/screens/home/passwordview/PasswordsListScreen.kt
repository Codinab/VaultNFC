package com.example.vaultnfc.ui.screens.home.passwordview

import PasswordsViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.ui.Screen


@Composable
fun PasswordsListScreen(
    navController: NavController,
    passwordsViewModel: PasswordsViewModel = viewModel(),
) {
    val passwordsList by passwordsViewModel.passwordsList.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            BackButton(navController = navController)
            PasswordsListView(passwordsList = passwordsList, onRemove = passwordsViewModel::removePassword)
        }
        AddPasswordFab(navController = navController)
    }
}

@Composable
fun BackButton(navController: NavController) {
    Button(onClick = { navController.navigateUp() }) {
        Text("Back")
    }
}

@Composable
fun AddPasswordFab(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddPassword.route) },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add New Password")
        }
    }
}


@Composable
fun PasswordsListView(passwordsList: List<PasswordItem>, onRemove: (PasswordItem) -> Unit) {
    val passwordsNoFolder = passwordsList.filter { it.folderName.isEmpty() }
    val passwordsInFolders = passwordsList.filterNot { it.folderName.isEmpty() }
    val groupedPasswords = passwordsInFolders.groupBy { it.folderName ?: "Default Folder" }

    LazyColumn {
        items(passwordsNoFolder) { password ->
            PasswordItemView(password = password, onRemove = onRemove)
        }
        groupedPasswords.forEach { (folderName, passwordsInFolder) ->
            item {
                FolderView(folderName, passwordsInFolder, onRemove)
            }
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
                    text = "Username: ${password.username}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                CopyIconButton(text = password.username, clipboardManager = clipboardManager, context = context)
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PasswordText(password = password, isPasswordVisible = isPasswordVisible, passwordsViewModel = passwordsViewModel)
                TogglePasswordVisibilityIconButton(isPasswordVisible = isPasswordVisible) {
                    isPasswordVisible = !isPasswordVisible
                }
                CopyIconButton(text = password.encryptedPassword, clipboardManager = clipboardManager, context = context)
            }
            Spacer(Modifier.height(8.dp))
            RemoveButton(onRemove = { onRemove(password) })
        }
    }
}

@Composable
fun PasswordText(password: PasswordItem, isPasswordVisible: Boolean, passwordsViewModel: PasswordsViewModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isPasswordVisible) {
            Text(
                text = "Password: ${passwordsViewModel.decryptPassword(password.encryptedPassword, "Test")}",
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
    }
}

@Composable
fun RemoveButton(onRemove: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),

    ) {
        Button(
            onClick = { onRemove() }
        ) {
            Text("Remove")
        }
    }
}
