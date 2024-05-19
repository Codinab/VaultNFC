package com.example.vaultnfc.ui.screens.home

import PasswordsViewModel
import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vaultnfc.R
import com.example.vaultnfc.data.repository.PasswordSelected.passwordItemSelected
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.components.BackgroundImageWrapper
import com.example.vaultnfc.ui.theme.BlackEnd
import com.example.vaultnfc.ui.theme.ButtonRed
import com.example.vaultnfc.ui.theme.RedEnd
import com.example.vaultnfc.ui.theme.WhiteEnd
import com.example.vaultnfc.ui.viewmodel.LoginViewModel
import com.example.vaultnfc.ui.viewmodel.MyBluetoothServiceViewModel
import com.example.vaultnfc.ui.viewmodel.TagViewModel
import com.example.vaultnfc.ui.viewmodel.TagViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PasswordsScreen(navController: NavController, application: Application) {
    BackgroundImageWrapper {

        var isSidebarOpen by remember { mutableStateOf(false) }
        val passwordsViewModel: PasswordsViewModel = viewModel()
        val passwordsList by passwordsViewModel.tagFilteredPasswords.observeAsState(emptyList())
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        var showMenu by remember { mutableStateOf(false) }
        val myBluetoothServiceViewModel: MyBluetoothServiceViewModel = viewModel(
            factory = MyBluetoothServiceViewModel.MyBluetoothServiceViewModelFactory(application)
        )

        DisposableEffect(currentRoute) {
            if (currentRoute == Screen.Home.route) passwordsViewModel.fetch()
            onDispose { }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { isSidebarOpen = true },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "", tint = ButtonRed)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logo_menu),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(passwordsList.size) { index ->
                        val password = passwordsList[index]
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clickable {
                                    passwordItemSelected = password
                                    navController.navigate(Screen.PasswordDetails.route)
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = password.title,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(text = password.username, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                        if (index != passwordsList.size - 1) Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "", tint = MaterialTheme.colorScheme.tertiary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset(160.dp, -70.dp)
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            stringResource(R.string.create_password),
                            fontWeight = FontWeight.Bold
                        )
                    }, onClick = {
                        showMenu = false
                        navController.navigate(Screen.AddPassword.route)
                    })
                    DropdownMenuItem(text = {
                        Text(
                            stringResource(R.string.receive_via_bluetooth),
                            fontWeight = FontWeight.Bold
                        )
                    }, onClick = {
                        showMenu = false
                        navController.navigate(Screen.BluetoothServer.route)
                    })
                }
            }
        }
        if (isSidebarOpen) SideBar(onClose = { isSidebarOpen = false }, navController, passwordsViewModel)
    }
}



@Composable
fun SideBar(onClose: () -> Unit, navController: NavController, passwordsViewModel: PasswordsViewModel) {
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 200.dp, max = 250.dp)
                .background(color = MaterialTheme.colorScheme.secondary)
                .clickable { /* do nothing on the sidebar itself */ }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 1.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.dp),
                        contentAlignment = Alignment.Center // Aligning the image to the center vertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_menu),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary))
                    Text("FOLDERS", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary))
                    Tags(passwordsViewModel)
                }
                Column(modifier = Modifier.padding(8.dp)) {
                    val buttonData = listOf(
                        ButtonData(stringResource(R.string.password_generator_MAYUS)) { navController.navigate(Screen.PasswordGenerator.route) },
                        ButtonData(stringResource(R.string.settings_MAYUS)) { navController.navigate(Screen.Settings.route) },
                        ButtonData(stringResource(R.string.log_out)) {
                            loginViewModel.logout()
                            navController.navigate(Screen.Opening.route)
                        }
                    )
                    buttonData.forEach { (text, action) ->
                        TextButton(onClick = action, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text,
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
}

@Composable
private fun Tags(passwordsViewModel: PasswordsViewModel) {
    val tagViewModel: TagViewModel = viewModel(factory = TagViewModelFactory(passwordsViewModel))
    val tags by tagViewModel.tags.observeAsState(emptyList())

    if (tags.isEmpty()) {
        Text("No tags available")
    } else {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = WhiteEnd)
            ) {
                TextButton(onClick = { passwordsViewModel.removeTag() }) {
                    Text(
                        text = "ALL PASSWORDS",
                        modifier = Modifier
                            .padding(8.dp),
                        color = BlackEnd
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            for (tag in tags) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = WhiteEnd)
                ) {
                    TextButton(onClick = { passwordsViewModel.setTag(tag) }) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(8.dp),
                            color = BlackEnd
                        )
                    }
                }
            }
        }
    }
}


data class ButtonData(val text: String, val action: () -> Unit)


