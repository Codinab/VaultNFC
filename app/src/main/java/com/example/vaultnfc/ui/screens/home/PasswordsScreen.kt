package com.example.vaultnfc.ui.screens.home

import PasswordsViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.theme.BlackEnd
import com.example.vaultnfc.ui.theme.ButtonRed
import com.example.vaultnfc.ui.theme.LightRed
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.theme.WhiteEnd
import com.example.vaultnfc.ui.viewmodel.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PasswordsScreen(navController: NavController) {
    var isSidebarOpen by remember { mutableStateOf(false) }

    val passwordsViewModel: PasswordsViewModel = viewModel()
    val passwordsList by passwordsViewModel.passwordsList.observeAsState(emptyList())

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMenu by remember { mutableStateOf(false) }

    DisposableEffect(currentRoute) {
        if (currentRoute == Screen.Home.route) {
            passwordsViewModel.fetch()
        }

        onDispose { }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Box( // Top bar
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(color = WhiteEnd), // Set background color to match the FloatingActionButton color
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { isSidebarOpen = true },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = ButtonRed // Set color of the menu icon
                    )
                }

                // Center-aligned logo
                Image(
                    painter = painterResource(id = R.drawable.logo_menu),
                    contentDescription = "Logo",
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(color = Color.Red)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(passwordsList.size) { index ->
                    val password = passwordsList[index]
                    val backgroundColor = Color.White

                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .background(color = backgroundColor)
                            .clickable { navController.navigate(Screen.PasswordDetails.route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = password.title,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = password.username,
                                color = Color.Black
                            )
                        }
                    }

                    if (index != passwordsList.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(color = Color.LightGray)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding applied here affects both FAB and DropdownMenu
            contentAlignment = Alignment.BottomEnd // Aligns children to the bottom end
        ) {
            // Floating Action Button for adding new password, positioned at the bottom right
            FloatingActionButton(
                onClick = { showMenu = !showMenu }, // Toggle the visibility of the menu
                modifier = Modifier.padding(16.dp),
                containerColor = ButtonRed // Set background color of the FloatingActionButton
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Password")
            }

            // DropdownMenu for selecting actions
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(160.dp, -70.dp)
            ) {
                // Option to add a new password
                DropdownMenuItem(text = { Text("Create password", fontWeight = FontWeight.Bold) }, onClick = {
                    showMenu = false // Dismiss the menu
                    navController.navigate(Screen.AddPassword.route) // Navigate to AddPassword screen
                })
                // Option to receive a password via Bluetooth
                DropdownMenuItem(text = { Text("Receive via Bluetooth", fontWeight = FontWeight.Bold)}, onClick = {
                    showMenu = false // Dismiss the menu
                    // Implement your logic to start receiving a password via Bluetooth
                    // This might involve navigating to another screen or opening a dialog
                })
            }
        }

        if (isSidebarOpen) {
            SideBar(
                onClose = { isSidebarOpen = false }, navController
            )
        }
    }
}



@Composable
fun SideBar(onClose: () -> Unit, navController: NavController) {
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray.copy(alpha = 0.5f))
            .clickable { onClose() }, contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 200.dp, max = 250.dp)
            .background(color = Color.White)
            .clickable { /* do nothing on the sidebar itself */ }) {

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 1.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 1.dp),
                            contentAlignment = Alignment.Center // Aligning the image to the center vertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_menu), // Replace 'your_image' with your actual image resource
                                contentDescription = null, modifier = Modifier.size(100.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(color = Color.Red)
                        )
                        Text("FOLDERS" ,modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                        Spacer(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(color = Color.Red)
                        )

                        repeat(20) { index -> // Example of 20 items, replace with your folder items

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = WhiteEnd)
                            ) {
                                TextButton(onClick = { navController.navigate(Screen.Home.route) }) {
                                    Text(
                                        "Folder $index",
                                        modifier = Modifier.padding(8.dp),
                                        color = BlackEnd
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = Color.Red)
                )
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Password Generator Button
                    TextButton(
                        onClick = { navController.navigate(Screen.PasswordGenerator.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "PASSWORD GENERATOR",
                            color = RedEnd,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Settings Button
                    TextButton(
                        onClick = { /* Handle settings button click */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "SETTINGS",
                            color = RedEnd,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Log Out Button
                    TextButton(
                        onClick = {
                            loginViewModel.logout(context)
                            navController.navigate(Screen.Opening.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "LOG OUT",
                            color = RedEnd,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

