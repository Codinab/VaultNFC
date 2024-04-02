package com.example.vaultnfc.ui.screens.home.passwordview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vaultnfc.model.PasswordItem

@Composable
fun FolderView(folderName: String, passwordsInFolder: List<PasswordItem>, onRemove: (PasswordItem) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }, // Make the card clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Text(
                text = folderName, // Use "Default Folder" for passwords not in a specific folder
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            if (isExpanded) {
                passwordsInFolder.forEach { password ->
                    PasswordItemView(password = password, onRemove = onRemove)
                }
            }
        }
    }
}

