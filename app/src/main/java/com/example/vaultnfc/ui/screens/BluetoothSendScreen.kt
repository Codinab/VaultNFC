package com.example.vaultnfc.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel
import com.example.vaultnfc.ui.viewmodel.PermissionViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothClientScreen(application: Application, navController: NavController) {

    PermissionsAndFeaturesSetup(viewModel = PermissionViewModel(application))

    val viewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )

    val discoveredDevices by viewModel.discoveredDevices.observeAsState(initial = emptyList())
    val isConnected by viewModel.isConnected.observeAsState()
    val toastMessages by viewModel.toastMessages.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Text("Client Screen")
        Text("Status: $toastMessages")

        Button(
            onClick = { viewModel.startDiscovery() },
            colors = ButtonDefaults.buttonColors(RedEnd),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp)
                .heightIn(min = 36.dp)
                .widthIn(min = 100.dp)
                .shadow(18.dp, RoundedCornerShape(1.dp)),
            shape = RoundedCornerShape(1.dp)
        ) {
            Text("Discover Devices")
        }

        Button(
            onClick = {
                viewModel.disconnect()
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(RedEnd),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp, bottom = 14.dp)
                .heightIn(min = 36.dp)
                .widthIn(min = 100.dp)
                .shadow(18.dp, RoundedCornerShape(1.dp)),
            shape = RoundedCornerShape(1.dp)
        ) {
            Text("Cancel")
        }

        HorizontalDivider(
            color = Color.Red
        )

        Button(
            onClick = { viewModel.send() },
            enabled = isConnected == true,
            colors = ButtonDefaults.buttonColors(RedEnd),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp, bottom = 14.dp)
                .heightIn(min = 36.dp)
                .widthIn(min = 100.dp)
                .shadow(18.dp, RoundedCornerShape(1.dp)),
            shape = RoundedCornerShape(1.dp)
        ) {
            Text("Send Password")
        }

        if (isConnected == false) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Discovered Devices")
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
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

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onDeviceClicked: (BluetoothDevice) -> Unit) {
    // Using Modifier.clickable to handle clicks on the entire row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClicked(device) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Display device name; if null, show "Unknown Device"
            Text(text = device.name ?: "Unknown Device", fontWeight = FontWeight.Bold)
            // Display device address
            Text(text = device.address, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = "Bluetooth Device",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


