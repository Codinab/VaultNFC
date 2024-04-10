package com.example.vaultnfc.ui.screens


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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel
import com.example.vaultnfc.ui.viewmodel.PermissionViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothServerScreen(application: Application, navController: NavController) {
    PermissionsAndFeaturesSetup(viewModel = PermissionViewModel(application))

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
            viewModel.disconnect()
        }
    }

    Surface(
        color = Color.White, // Set the background color
        modifier = Modifier.fillMaxSize(), // Fill the entire available space
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                // Center content horizontally // Center content vertically
            ) {
                Text(
                    text = "Server Screen",
                )
                Text(
                    text = "Status: $toastMessages"
                )

                Button(
                    onClick = {
                        viewModel.enableDiscoverability(discoverableLauncher)
                    },
                    colors = ButtonDefaults.buttonColors(RedEnd),
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .shadow(18.dp, RoundedCornerShape(1.dp)),
                    shape = RoundedCornerShape(1.dp)
                ) {
                    Text(stringResource(R.string.start_receiving))
                }

                Button(
                    onClick = {
                        viewModel.disconnect()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(RedEnd),
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .shadow(18.dp, RoundedCornerShape(1.dp)),
                    shape = RoundedCornerShape(1.dp)
                ) {
                    Text("Back")
                }

                // Display the latest toast message

                if (passwordItem != null) {
                    val passwordItemName = passwordItem!!.title
                    Text(
                        text = "Password Item Received: $passwordItemName"
                    )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),

                            horizontalArrangement = Arrangement.Center, // Add horizontal spacing between elements
                            verticalAlignment = Alignment.CenterVertically, // Align elements vertically in the center
                            // Add horizontal spacing between elements
                        ) {
                            Button(
                                onClick = {
                                    passwordsViewModel.addPasswordItem(passwordItem!!)
                                    viewModel.disconnect()
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(RedEnd),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 20.dp)
                                    .heightIn(min = 36.dp)
                                    .widthIn(min = 100.dp)
                                    .shadow(18.dp, RoundedCornerShape(1.dp)),
                                shape = RoundedCornerShape(1.dp),

                                // Align the button vertically in the center
                            ) {
                                Text("Accept")
                            }

                            Button(
                                onClick = {
                                    viewModel.disconnect()
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(RedEnd),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 20.dp)
                                    .heightIn(min = 36.dp)
                                    .widthIn(min = 100.dp)
                                    .shadow(18.dp, RoundedCornerShape(1.dp)),
                                shape = RoundedCornerShape(1.dp)// Align the button vertically in the center
                            ) {
                                Text("Reject")
                            }
                        }
                }
            }
        }
    }
}
