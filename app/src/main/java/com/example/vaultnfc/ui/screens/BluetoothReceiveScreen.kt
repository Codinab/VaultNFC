package com.example.vaultnfc.ui.screens


import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel

@Composable
fun BluetoothServerScreen(application: Application) {
    val viewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )

    val toastMessages by viewModel.toastMessages.observeAsState()
    val isConnected by viewModel.isConnected.observeAsState()
    val passwordItem by viewModel.passwordItem.observeAsState()

    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.startServer()
        } else {
            Toast.makeText(application, "Discoverability denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Text("Server Screen")
        Text("Status: $toastMessages")

        Button(onClick = {
            viewModel.enableDiscoverability(discoverableLauncher)
            viewModel.startServer()
        }) {
            Text("Make Discoverable")
        }

        Button(onClick = { viewModel.disconnect() }) {
            Text("Cancel")
        }

        // Display the latest toast message

        // Handle the received messages
        if (isConnected == true && passwordItem != null) {
            val passwordItemName = passwordItem!!.title // Simulate extracting the name
            Text("Password Item Received: $passwordItemName")

            Button(onClick = { /* Handle acceptance */ }) {
                Text("Accept")
            }

            Button(onClick = { /* Handle rejection */ }) {
                Text("Reject")
            }
        }

    }
}
