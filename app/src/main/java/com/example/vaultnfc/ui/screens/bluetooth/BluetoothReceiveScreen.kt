package com.example.vaultnfc.ui.screens.bluetooth


import PasswordsViewModel
import android.app.Activity
import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
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
fun BluetoothServerScreen(application: Application, navController: NavController) {

    val permissionViewModel: PermissionViewModel = viewModel()
    PermissionsAndFeaturesSetup(viewModel = permissionViewModel)

    val bluetoothViewModel: MyBluetoothServiceViewModel = viewModel(
        factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
    )

    val toastMessages by bluetoothViewModel.toastMessages.observeAsState()
    val passwordItem by bluetoothViewModel.passwordItemToSave.observeAsState()

    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(application, "Discoverability denied", Toast.LENGTH_SHORT).show()
        } else {
            bluetoothViewModel.startServer()
        }
    }

    val passwordsViewModel = PasswordsViewModel(application)

    DisposableEffect(navController) {
        onDispose {
            bluetoothViewModel.disconnect()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        BackgroundImageWrapper {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Server Screen")
                Text(text = "Status: $toastMessages")

                ActionButton(
                    text = stringResource(R.string.start_receiving),
                    onClick = { bluetoothViewModel.enableDiscoverability(discoverableLauncher) }
                )

                ActionButton(
                    text = "Back",
                    onClick = {
                        bluetoothViewModel.disconnect()
                        navController.popBackStack()
                    }
                )

                passwordItem?.let {
                    Text(text = "Password Item Received: ${it.title}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AcceptRejectButton(
                            text = "Accept",
                            onClick = {
                                passwordsViewModel.addPasswordItem(it)
                                bluetoothViewModel.disconnect()
                                navController.popBackStack()
                            }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        AcceptRejectButton(
                            text = "Reject",
                            onClick = {
                                bluetoothViewModel.disconnect()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .heightIn(min = 36.dp)
            .shadow(18.dp, RoundedCornerShape(1.dp)),
        shape = RoundedCornerShape(1.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun AcceptRejectButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .heightIn(min = 36.dp)
            .widthIn(min = 100.dp)
            .shadow(18.dp, RoundedCornerShape(1.dp)),
        shape = RoundedCornerShape(1.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.tertiary)
    }
}

