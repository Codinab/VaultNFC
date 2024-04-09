package com.example.vaultnfc.ui.screens

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.vaultnfc.ui.viewmodel.PermissionViewModel
import com.example.vaultnfc.util.openAppSettings
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PermissionsAndFeaturesSetup(viewModel: PermissionViewModel) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    SetupBluetoothPermissionHandling(viewModel)
    EnableBluetoothFeature(viewModel)
    CheckAndPromptForGPSIfNeeded(viewModel, application)
}

@Composable
fun CheckAndPromptForGPSIfNeeded(viewModel: PermissionViewModel, application: Application) {
    val gpsEnabledEvent by viewModel.gpsEnabledEvent.observeAsState()

    gpsEnabledEvent?.getContentIfNotHandled()?.let { isEnabled ->
        if (!isEnabled) {
            ShowEnableGpsDialog(application) {
                navigateToLocationSettings(application)
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetupBluetoothPermissionHandling(viewModel: PermissionViewModel) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Remember a launcher for the permissions request
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { !it.value }) {
            viewModel.handlePermissionDenied()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.requestBluetoothPermissions()
    }

    val permissionRequestEvent by viewModel.bluetoothPermissionRequestEvent.observeAsState()
    permissionRequestEvent?.getContentIfNotHandled()?.let { permissions ->
        permissionLauncher.launch(permissions)
    }

    val navigateToSettingsEvent by viewModel.navigateToSettingsEvent.observeAsState()
    navigateToSettingsEvent?.getContentIfNotHandled()?.let {
        // Trigger navigation to the app settings. Since we can't navigate directly in Compose,
        // this would typically trigger an action that uses the context to start an intent.
        openAppSettings(application)
    }
}

@Composable
fun EnableBluetoothFeature(viewModel: PermissionViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Bluetooth has been enabled
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Bluetooth is required for some features",
                    actionLabel = "Settings",
                    duration = SnackbarDuration.Long
                ).let { actionResult ->
                    if (actionResult == SnackbarResult.ActionPerformed) {
                        openAppSettings(context)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        val bluetoothAdapter: BluetoothAdapter? = viewModel.bluetoothAdapter
        if (bluetoothAdapter?.isEnabled == false) {
            bluetoothEnableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    // Assuming you have a Scaffold or a similar structure where the SnackbarHost can live
    SnackbarHost(hostState = snackbarHostState)
}

@Composable
fun ShowEnableGpsDialog(application: Application, onNavigate: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { /* Handle dismissal */ },
        title = { Text("Enable GPS") },
        text = { Text("This app requires GPS to be enabled for full functionality. Please enable GPS in the settings.") },
        confirmButton = {
            Button(onClick = {
                onNavigate()
            }) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            Button(onClick = { /* Handle Dismiss */ }) {
                Text("Cancel")
            }
        }
    )
}

fun navigateToLocationSettings(application: Application) {
    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
    application.startActivity(intent)
}



