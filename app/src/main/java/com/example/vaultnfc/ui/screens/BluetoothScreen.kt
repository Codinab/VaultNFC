package com.example.vaultnfc.ui.screens

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vaultnfc.ui.viewmodel.BluetoothViewModel
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel

@Composable
fun BluetoothScreen(application: Application, viewModel: BluetoothViewModel) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.requestBluetoothPermissions()
        viewModel.checkAndEnableBluetooth()
    }

    // Setup for requesting Bluetooth permissions and handling Bluetooth enable prompts.
    SetupBluetoothPermissionHandling(viewModel, context)

    // Observe Bluetooth state changes and react accordingly.
    ObserveBluetoothStateChanges(viewModel, context)

    BluetoothChatScreen(application)


}

@Composable
fun BluetoothChatScreen(application: Application) {
    //init viewmodel with application
    val viewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )
    // Observe LiveData objects
    val readMessages by viewModel.readMessages.observeAsState()
    val writeMessages by viewModel.writeMessages.observeAsState()
    val toastMessages by viewModel.toastMessages.observeAsState()

    var inputText by remember { mutableStateOf("") }

    Column {
        // Display the latest message received
        Text("Received: ${String(readMessages ?: ByteArray(0))}")

        // Display the latest message sent confirmation
        Text("Sent: $writeMessages")

        // Display the latest toast message
        Text("Status: $toastMessages")

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Type your message here") },
        )

        Button(onClick = {
            viewModel.write(inputText.toByteArray())
            inputText = "" // Clear the input field after sending the message
        }) {
            Text("Send")
        }

        val discoveredDevices by viewModel.discoveredDevices.observeAsState(initial = emptyList())

        Button(onClick = { viewModel.startDiscovery() }) {
            Text("Discover Devices")
        }

        val discoverableLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                // Discoverability was not allowed by the user
                Log.d("BluetoothDiscoverable", "Discoverability denied.")
            } else {
                // Device is now discoverable
                Log.d(
                    "BluetoothDiscoverable",
                    "Device is discoverable for ${result.resultCode} seconds."
                )
                viewModel.startServer()
            }
        }

        Button(onClick = {viewModel.enableDiscoverability(discoverableLauncher) }) {
            Text("Start Server")
        }

        Column {
            Text("Discovered Devices")
            LazyColumn {
                items(discoveredDevices) { device ->
                    DeviceItem(device) { selectedDevice ->
                        viewModel.connectToDevice(selectedDevice)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: BluetoothDevice, onDeviceClicked: (BluetoothDevice) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onDeviceClicked(device) },
        headlineContent = {
            Column {
                Text(device.name ?: "Unknown Device")
                Text(device.address ?: "Unknown Address")
                Text("Bond State: ${device.bondState}")
            }

        }
    )
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




