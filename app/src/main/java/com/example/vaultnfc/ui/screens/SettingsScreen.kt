package com.example.vaultnfc.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, application: Application) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val isDarkThemeEnabled by settingsViewModel.darkThemeEnabled.collectAsState(initial = false)


    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            colors = ButtonDefaults.buttonColors(RedEnd),
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .size(width = 200.dp, height = 50.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "SETTINGS",

                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = RedEnd,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        NightMode(isDarkThemeEnabled, settingsViewModel)
        LogoutTimerOption(settingsViewModel)

        SettingsOption("Change Account Password", "Change your account password", application)
        SettingsOption("Notification Settings", "Configure notification preferences", application)
        SettingsOption("Language", "Change the language of the app", application)
    }
}

@Composable
private fun NightMode(
    isDarkThemeEnabled: Boolean,
    settingsViewModel: SettingsViewModel,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                "Night Mode",
            )
            Text(
                text = "Enable dark mode for the app",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = isDarkThemeEnabled,
            onCheckedChange = { isEnabled ->
                settingsViewModel.toggleDarkTheme(isEnabled)
            },
        )
    }
}

@Composable
fun LogoutTimerOption(settingsViewModel: SettingsViewModel) {
    val options = listOf("Never", "Closing app", "15 minutes", "1 day", "1 week")
    var showDialog by remember { mutableStateOf(false) }
    val selectedOption by settingsViewModel.logoutTimerOption.collectAsState(initial = "Never")

    if (showDialog) {
        // Implement your dialog or dropdown menu here
        // This is a placeholder for the actual UI component you choose to use
        LogoutTimerDialog(options, selectedOption, settingsViewModel) { selected ->
            settingsViewModel.setLogoutTimerOption(selected)
            showDialog = false
        }
    }

    LogoutOption(
        label = "Logout Preference",
        description = "Set the auto logout timer",
        selectedOption = selectedOption
    ) {
        showDialog = true
    }
}

@Composable
fun LogoutTimerDialog(
    options: List<String>,
    currentSelection: String,
    settingsViewModel: SettingsViewModel,
    onOptionSelected: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Handle dismiss */ },
        title = { Text("Select Logout Timer") },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = option == currentSelection,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            settingsViewModel.setLogoutTimerOption(currentSelection)
        },
    )
}


@Composable
fun SettingsOption(
    label: String,
    description: String,
    application: Application
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                Toast.makeText(application, "Not implemented", Toast.LENGTH_SHORT).show()
            }
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Navigate forward")
    }
}

@Composable
fun LogoutOption(
    label: String,
    description: String,
    selectedOption: String,
    onClick: () -> Unit = {
        Toast.makeText(null, "Not implemented", Toast.LENGTH_SHORT).show()
    },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Text(text = selectedOption, Modifier.padding(end = 15.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Navigate forward")
    }
}