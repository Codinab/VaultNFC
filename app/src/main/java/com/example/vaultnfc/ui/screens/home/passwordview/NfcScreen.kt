package com.example.vaultnfc.ui.screens.home.passwordview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.model.deserializePasswordItem
import com.example.vaultnfc.ui.viewmodel.NfcSharingViewModel
import com.example.vaultnfc.util.NfcManager

@Composable
fun NfcSendScreen(viewModel: NfcSharingViewModel, passwordItem: PasswordItem, nfcManager: NfcManager) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Serialize PasswordItem for NFC
    val nfcData = remember { viewModel.preparePasswordItemForNfc(passwordItem) }

    // Lifecycle observer to manage NFC
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Setup NFC for foreground dispatch or other operations
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // Cleanup or disable NFC to avoid leaks
                }
                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Example UI
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "NFC Data Ready", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Implement NFC sending logic here */ }) {
            Text("Send via NFC")
        }
    }
}

@Composable
fun NfcReceiveScreen(passwordItemJson: String?) {
    val passwordItem = passwordItemJson?.let { deserializePasswordItem(it) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Received NFC Data", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Title: ${passwordItem?.title ?: "N/A"}")
        // Display other fields as needed
    }
}

