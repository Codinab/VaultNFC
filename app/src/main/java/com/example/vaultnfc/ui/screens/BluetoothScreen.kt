package com.example.vaultnfc.ui.screens

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.vaultnfc.ui.viewmodel.BluetoothViewModel

@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.requestBluetoothPermissions()
        viewModel.checkAndEnableBluetooth()
    }

    // Setup for requesting Bluetooth permissions and handling Bluetooth enable prompts.
    SetupBluetoothPermissionHandling(viewModel, context)

    // Observe Bluetooth state changes and react accordingly.
    ObserveBluetoothStateChanges(viewModel, context)
}

@Composable
fun SetupBluetoothPermissionHandling(viewModel: BluetoothViewModel, context: Context) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Implementation details on permission result handling.
    }

    val permissionRequestEvent by viewModel.bluetoothPermissionRequestEvent.observeAsState()
    permissionRequestEvent?.getContentIfNotHandled()?.let { permissions ->
        permissionLauncher.launch(permissions)
    }
}

@Composable
fun ObserveBluetoothStateChanges(viewModel: BluetoothViewModel, context: Context) {
    // Observe Bluetooth state change event.
    val bluetoothStateChangeEvent by viewModel.bluetoothStateChangeEvent.observeAsState()
    bluetoothStateChangeEvent?.getContentIfNotHandled()?.let { state ->
        when (state) {
            BluetoothAdapter.STATE_OFF -> ShowEnableBluetoothDialog(context)
            // Handle other states if necessary.
        }
    }
}

@Composable
fun ShowEnableBluetoothDialog(context: Context) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enable Bluetooth") },
            text = { Text("This app requires Bluetooth to be enabled. Please enable Bluetooth to continue.") },
            confirmButton = {
                Button(onClick = {
                    context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    showDialog = false
                }) {
                    Text("Enable Bluetooth")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



