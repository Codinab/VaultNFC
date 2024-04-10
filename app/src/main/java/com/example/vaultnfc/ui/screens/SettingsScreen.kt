package com.example.vaultnfc.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun SettingsScreen(navController: NavController) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val isDarkThemeEnabled by settingsViewModel.darkThemeEnabled.collectAsState(initial = false)


    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            colors = ButtonDefaults.buttonColors(RedEnd),
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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

        SettingsOption("Change Account Password", "Change your account password")
        SettingsOption("Notification Settings", "Configure notification preferences")
        SettingsOption("Language", "Change the language of the app")
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
fun SettingsOption(
    label: String,
    description: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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