package com.example.vaultnfc.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vaultnfc.ui.theme.RedEnd

@Composable
fun BackButton(navController: NavController) {
    Button(
        colors = ButtonDefaults.buttonColors(RedEnd),
        onClick = { navController.navigateUp() },
        modifier = Modifier
            .padding(16.dp),
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}