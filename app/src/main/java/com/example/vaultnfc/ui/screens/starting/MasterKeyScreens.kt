package com.example.vaultnfc.ui.screens.starting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.screens.home.passwordview.inputField
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.viewmodel.MasterKeyViewModel
import kotlinx.coroutines.delay

@Composable
fun InitialMasterKeyScreen(navController: NavController) {
    val masterKeyViewModel: MasterKeyViewModel = viewModel()
    var masterKey by remember { mutableStateOf("") }
    val masterKeyError by masterKeyViewModel.masterKeyError.observeAsState()
    val blockUser by masterKeyViewModel.blockUser.collectAsState()
    val blockEndTimeFormatted by remember { mutableStateOf(masterKeyViewModel.getBlockEndTimeFormatted()) }
    var timeUntilUnblockedFormatted by remember { mutableStateOf(masterKeyViewModel.getTimeUntilUnblockedFormatted()) }
    val savedMasterKey = masterKeyViewModel.isMasterKeySet.observeAsState()

    LaunchedEffect(blockUser) {
        while (blockUser) {
            timeUntilUnblockedFormatted = masterKeyViewModel.getTimeUntilUnblockedFormatted()
            delay(3000L) // Update every 3 seconds
        }
    }

    if (savedMasterKey.value == true) {
        navController.navigate(Screen.Home.route)
        return
    }

    if (blockUser) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Too many attempts. Please wait before trying again.",
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "You will be unblocked at: $blockEndTimeFormatted",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Time remaining: $timeUntilUnblockedFormatted",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Enter Master Key", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = masterKey,
                onValueChange = { masterKey = it },
                label = { Text("Master Key") },
                isError = masterKeyError != null
            )

            if (masterKeyError != null) {
                Text(text = masterKeyError ?: "", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (masterKey.isEmpty()) {
                    masterKeyViewModel.masterKeyError.postValue("Master key cannot be empty")
                } else {
                    masterKeyViewModel.saveMasterKey(masterKey)
                    navController.navigate(Screen.Home.route)
                }
            }) {
                Text("Submit")
            }
        }
    }
}


@Composable
fun ChangeMasterKeyScreen(navController: NavController) {
    val masterKeyViewModel: MasterKeyViewModel = viewModel()
    var newMasterKey by remember { mutableStateOf("") }
    val masterKeyError by masterKeyViewModel.masterKeyError.observeAsState()
    val blockUser by masterKeyViewModel.blockUser.collectAsState()
    val blockEndTimeFormatted = masterKeyViewModel.getBlockEndTimeFormatted()
    val timeUntilUnblockedFormatted = masterKeyViewModel.getTimeUntilUnblockedFormatted()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Back button at the top left corner
        Button(
            colors = ButtonDefaults.buttonColors(RedEnd),
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        // Main content in the center
        if (blockUser) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Too many attempts. Please wait before trying again.",
                        color = Color.Red,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You will be unblocked at: $blockEndTimeFormatted",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Time remaining: $timeUntilUnblockedFormatted",
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Change Master Key", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                inputField(
                    value = newMasterKey,
                    icon = Icons.Default.Lock,
                    labelId = R.string.master_key_label,
                    iconDescId = R.string.master_key_icon_desc,
                    onValueChange = { newMasterKey = it }
                )

                if (masterKeyError != null) {
                    Text(text = masterKeyError ?: "", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    masterKeyViewModel.saveMasterKey(newMasterKey)
                    navController.popBackStack()
                    navController.popBackStack()
                }) {
                    Text("Change Master Key")
                }
            }
        }
    }
}
