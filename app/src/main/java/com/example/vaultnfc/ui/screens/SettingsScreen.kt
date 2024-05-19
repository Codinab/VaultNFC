package com.example.vaultnfc.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.components.BackgroundImageWrapper
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, application: Application) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val isDarkThemeEnabled by settingsViewModel.darkThemeEnabled.collectAsState(initial = false)
    BackgroundImageWrapper {
        Column(modifier = Modifier.padding(16.dp)) {
            BackButton(navController)

            TitleBox(stringResource(R.string.settings_2))

            Spacer(modifier = Modifier.height(32.dp))

            NightModeOption(isDarkThemeEnabled) {
                settingsViewModel.toggleDarkTheme(it)
            }

            LogoutTimerOption(settingsViewModel)

            SettingsOptionRedirect(
                stringResource(R.string.change_master_key),
                stringResource(R.string.change_the_master_key_used_for_your_data),
            ) {
                navController.navigate(Screen.ChangeMasterPassword.route)
            }

            SettingsOption(
                stringResource(R.string.change_account_password),
                stringResource(R.string.change_your_account_password), application
            )
            SettingsOption(
                stringResource(R.string.notification_settings),
                stringResource(R.string.configure_notification_preferences), application
            )
            SettingsOption(
                stringResource(R.string.language),
                stringResource(R.string.change_the_language_of_the_app), application
            )
        }
    }
}

@Composable
fun BackButton(navController: NavController) {
    Button(
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        onClick = { navController.navigateUp() },

    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun TitleBox(title: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .size(width = 200.dp, height = 50.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun NightModeOption(isDarkThemeEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    OptionRow(
        title = stringResource(R.string.night_mode),
        description = stringResource(R.string.enable_dark_mode_for_the_app)
    ) {
        Switch(
            checked = isDarkThemeEnabled,
            onCheckedChange = onToggle,
        )
    }
}

@Composable
fun LogoutTimerOption(settingsViewModel: SettingsViewModel) {
    val options = SettingsViewModel.TIMEOUT_MODE
    var showDialog by remember { mutableStateOf(false) }
    val selectedOption by settingsViewModel.logoutTimerOption.collectAsState(initial = stringResource(
        R.string.never
    )
    )

    if (showDialog) {
        LogoutTimerDialog(options, selectedOption, settingsViewModel) { selected ->
            settingsViewModel.setLogoutTimerOption(selected)
            showDialog = false
        }
    }

    LogoutOption(
        label = stringResource(R.string.logout_preference),
        description = stringResource(R.string.set_the_auto_logout_timer),
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
    BackgroundImageWrapper {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.select_logout_timer)) },
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
                                onClick = { onOptionSelected(option) },

                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { settingsViewModel.setLogoutTimerOption(currentSelection) }) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.tertiary)
                }
            }
        )
    }
}

@Composable
fun MasterKeyTimerDialog(
    options: List<String>,
    currentSelection: String,
    settingsViewModel: SettingsViewModel,
    onOptionSelected: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Handle dismiss */ },
        title = { Text("Select MasterKey Timer") },
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
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        },
        confirmButton = {
            settingsViewModel.setMasterKeyTimerOption(currentSelection)
        },
    )
}

@Composable
fun SettingsOptionRedirect(
    label: String,
    description: String,
    onClick: () -> Unit
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
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 12.sp
            )
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "", tint = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun SettingsOption(label: String, description: String, application: Application) {

    OptionRow(
        title = label,
        description = description,
        onClick = {
            Toast.makeText(application, "Not implemented", Toast.LENGTH_SHORT).show()
        }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "", tint = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun LogoutOption(label: String, description: String, selectedOption: String, onClick: () -> Unit) {
    OptionRow(
        title = label,
        description = description,
        onClick = onClick
    ) {
        Text(text = selectedOption, Modifier.padding(end = 15.dp), color = MaterialTheme.colorScheme.tertiary)
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.navigate_forward), tint = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun OptionRow(
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.tertiary)
            Text(text = description, color = MaterialTheme.colorScheme.outline, fontSize = 12.sp)
        }
        content()
    }
}
