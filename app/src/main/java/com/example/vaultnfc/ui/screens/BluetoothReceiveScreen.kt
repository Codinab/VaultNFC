package com.example.vaultnfc.ui.screens


import PasswordsViewModel
import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel

@Composable
fun BluetoothServerScreen(application: Application, navController: NavController) {
    val viewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )

    val toastMessages by viewModel.toastMessages.observeAsState()
    val passwordItem by viewModel.passwordItemToSave.observeAsState()

    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(application, "Discoverability denied", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.startServer()
        }
    }

    val passwordsViewModel: PasswordsViewModel = viewModel()

    DisposableEffect(navController) {

        onDispose {
            // This block will be called when the composable is being removed from the composition
            // or when the key (navController in this case) changes.
            viewModel.disconnect()
        }
    }

    Column {
        Text("Server Screen")
        Text("Status: $toastMessages")

        Row {
            Button(onClick = {
                viewModel.enableDiscoverability(discoverableLauncher)
            }) {
                Text("Start Receiving")
            }
        }



        Button(onClick = {
            viewModel.disconnect()
            navController.popBackStack()
        }) {
            Text("Back")
        }

        // Display the latest toast message

        // Handle the received messages
        if (passwordItem != null) {
            val passwordItemName = passwordItem!!.title // Simulate extracting the name
            Text("Password Item Received: $passwordItemName")

            Button(onClick = {
                passwordsViewModel.addPasswordItem(passwordItem!!)
                viewModel.disconnect()
                navController.popBackStack()
            }) {
                Text("Accept")
            }

            Button(onClick = {
                viewModel.disconnect()
                navController.popBackStack()
            }) {
                Text("Reject")
            }
        }

    }
}
