package com.example.vaultnfc.ui.screens.home

import PasswordsViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vaultnfc.R
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.theme.BlackEnd
import com.example.vaultnfc.ui.theme.RedEnd

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    var isSidebarOpen by remember { mutableStateOf(false) }

    val passwordsViewModel: PasswordsViewModel = viewModel()
    val passwordsList by passwordsViewModel.passwordsList.observeAsState(emptyList())

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    DisposableEffect(currentRoute) {
        if (currentRoute == Screen.Home.route) {
            passwordsViewModel.fetch()
        }

        onDispose { }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column {
            Box(                    //Header
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(color = Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(             //Button list
                        onClick = { isSidebarOpen = true },
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        Icon(Icons.Filled.FormatListNumbered,
                            contentDescription = "Copy Username",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Red
                        )
                    }
                    Image( //Logo
                        painter = painterResource(id = R.drawable.logo_menu),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(horizontal = 10.dp)
                    )
                    TextButton(             //Button add password
                        onClick = { navController.navigate(Screen.AddPassword.route) },
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        Icon(Icons.Filled.Add,
                            contentDescription = "Add button",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Red
                        )
                    }
                }
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
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color.Red)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                append("${password.title}\n")
                            }
                            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                                append(password.username)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                // Your click action here
                            }

                    )

                }
            }
        }

        if (isSidebarOpen) {
            SideBar(
                onClose = { isSidebarOpen = false },
                navController
            )
        }
    }
}

@Composable
fun SideBar(onClose: () -> Unit, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 200.dp, max = 250.dp)
                .background(color = Color.White)
                .clickable { /* do nothing on the sidebar itself */ }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
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
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(color = Color.Red)
                        )
                        Text("FOLDERS", modifier = Modifier.padding(8.dp))
                        Spacer(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(color = Color.Red)
                        )

                        repeat(20) { // Example of 20 items, replace with your folder items
                            TextButton(
                                onClick = { navController.navigate(Screen.Home.route) }
                            ) {
                                Text("Folder $it", modifier = Modifier.padding(8.dp), color = BlackEnd)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color.Red)
                    )
                    TextButton(
                        onClick = { navController.navigate(Screen.PasswordGenerator.route)}
                    ) {
                        Text("PASSWORD GENERATOR", color = RedEnd, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Handle settings button click */ }
                    ) {
                        Text("SETTINGS", color = RedEnd, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Handle logout button click */ }
                    ) {
                        Text("LOG OUT", color = RedEnd, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}