package com.example.vaultnfc.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel

@Composable
fun BluetoothClientScreen(application: Application, navController: NavController) {
    val viewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )

    val discoveredDevices by viewModel.discoveredDevices.observeAsState(initial = emptyList())
    val isConnected by viewModel.isConnected.observeAsState()
    val toastMessages by viewModel.toastMessages.observeAsState()

    Column {
        Text("Client Screen")
        Text("Status: $toastMessages")


        Button(onClick = { viewModel.startDiscovery() }) {
            Text("Discover Devices")
        }

        Button(onClick = {
            viewModel.disconnect()
            navController.popBackStack()
        }) {
            Text("Cancel")
        }

        HorizontalDivider()

        Button(onClick = { viewModel.send() }, enabled = isConnected == true){
            Text("Send Password")
        }

        if (isConnected == false) {
            Column {
                Text("Discovered Devices")
                LazyColumn {
                    items(discoveredDevices) { device ->
                        DeviceItem(device) { selectedDevice ->
                            viewModel.connectToDevice(selectedDevice)
                            // Assuming you implement sending the password upon connection
                        }
                    }
                }
            }
        }
    }
}
