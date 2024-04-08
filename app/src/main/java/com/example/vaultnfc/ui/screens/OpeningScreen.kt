package com.example.vaultnfc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.data.repository.SecureStorage
import com.example.vaultnfc.ui.Screen
import com.example.vaultnfc.ui.theme.BlackEnd
import com.example.vaultnfc.ui.theme.WhiteEnd
import com.example.vaultnfc.ui.viewmodel.LoginViewModel


@Composable
fun OpeningScreen(navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val loginViewModel: LoginViewModel = viewModel()


    LaunchedEffect(key1 = true) {
        // Attempt to retrieve saved credentials
        val (savedEmail, savedPassword) = SecureStorage.getLoginDetails(context)
        // If credentials are found, attempt to log in
        if (savedEmail != null && savedPassword != null) {
            loginViewModel.login(savedEmail, savedPassword, context) {
                // Upon successful login, navigate to the home screen
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Opening.route) { inclusive = true }
                }
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_logging),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to_vaultnfc),
                fontSize = 24.sp,
                color = WhiteEnd,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(3f) // Set aspect ratio to make it square
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = {
                        isClicked = !isClicked
                        navController.navigate(Screen.Home.route)
                    },
                    colors = ButtonDefaults.buttonColors(WhiteEnd),
                    shape = RoundedCornerShape(1.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(45.dp)
                        .align(Alignment.Center)
                        // Ensure same width for both buttons
                        .width(IntrinsicSize.Max) // Ensure same height for both buttons
                        .shadow(3.dp, RoundedCornerShape(1.dp)),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.l_google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Login with Google", color = BlackEnd)
                    }
                }
            }

            Button(
                onClick = {
                    isClicked = !isClicked
                    navController.navigate(Screen.Login.route)
                },
                colors = ButtonDefaults.buttonColors(WhiteEnd),
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp)
                    .width(IntrinsicSize.Max) // Ensure same width for both buttons // Ensure same height for both buttons
                    .shadow(3.dp, RoundedCornerShape(1.dp)),
                shape = RoundedCornerShape(1.dp)
            ) {
                Text(text = "Login with account", color = BlackEnd)
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Create new account",
                color = Color.White,
                modifier = Modifier.clickable(onClick = { navController.navigate(Screen.Register.route) })
            )
        }
    }
}