package com.example.vaultnfc.ui.screens.bluetooth

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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.components.BackgroundImageWrapper
import com.example.vaultnfc.ui.screens.PermissionsAndFeaturesSetup
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel
import com.example.vaultnfc.ui.viewmodel.PermissionViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothClientScreen(application: Application, navController: NavController) {
    BackgroundImageWrapper {

        val permissionViewModel: PermissionViewModel = viewModel()
        PermissionsAndFeaturesSetup(viewModel = permissionViewModel)

        val viewModel: MyBluetoothServiceViewModel = viewModel(
            factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
        )

        val discoveredDevices by viewModel.discoveredDevices.observeAsState(initial = emptyList())
        val isConnected by viewModel.isConnected.observeAsState()
        val toastMessages by viewModel.toastMessages.observeAsState()
        var encryptionKey by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            Text(stringResource(R.string.client_screen), color = MaterialTheme.colorScheme.tertiary)
            Text("Status: $toastMessages", color = MaterialTheme.colorScheme.tertiary)

            ActionButton(
                text = (stringResource(R.string.discover_devices)),
                onClick = { viewModel.startDiscovery() }
            )

            ActionButton(
                text = stringResource(R.string.cancel),
                onClick = {
                    viewModel.disconnect()
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            HorizontalDivider(color = Color.Red)

            // Encryption key input
            TextField(
                value = encryptionKey,
                onValueChange = {
                    encryptionKey = it
                },
                label = { Text(stringResource(R.string.encryption_key)) },
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            ActionButton(
                text = stringResource(R.string.send_password),
                onClick = { viewModel.send() },
                enabled = isConnected == true && encryptionKey.isNotEmpty(),
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            if (isConnected == false) {
                DiscoveredDevicesList(
                    discoveredDevices = discoveredDevices,
                    onDeviceClicked = { device ->
                        viewModel.connectToDevice(device)
                    }
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = modifier
            .heightIn(min = 36.dp)
            .widthIn(min = 100.dp)
            .shadow(18.dp, RoundedCornerShape(1.dp)),
        shape = RoundedCornerShape(1.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun DiscoveredDevicesList(
    discoveredDevices: List<BluetoothDevice>,
    onDeviceClicked: (BluetoothDevice) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            stringResource(R.string.discovered_devices),
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn {
            items(discoveredDevices) { device ->
                DeviceItem(device = device, onDeviceClicked = onDeviceClicked)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onDeviceClicked: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClicked(device) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name ?: stringResource(R.string.unknown_device),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}



