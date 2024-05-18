package com.example.vaultnfc.ui.screens.starting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.vaultnfc.MainActivity
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

    LaunchedEffect(Unit) {

        if (loginViewModel.isAuth()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }

        val (savedEmail, savedPassword) = SecureStorage.getLoginDetails(context)
        if (savedEmail != null && savedPassword != null) {
            loginViewModel.login(savedEmail, savedPassword, context) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            AuthButton(
                text = "Login with Github",
                onClick = {
                    isClicked = !isClicked
                    loginViewModel.loginWithGitHub(activity = context as MainActivity) {
                        System.out.println("Logged in with GitHub")
                        navController.navigate(Screen.Home.route)
                    }
                },
                iconResId = R.drawable.ic_github
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthButton(
                text = "Login with account",
                onClick = {
                    isClicked = !isClicked
                    navController.navigate(Screen.Login.route)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Create new account",
                color = Color.White,
                modifier = Modifier.clickable { navController.navigate(Screen.Register.route) }
            )
        }
    }
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    iconResId: Int? = null
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(WhiteEnd),
        shape = RoundedCornerShape(1.dp),
        modifier = Modifier
            .width(200.dp)
            .height(45.dp)
            .shadow(3.dp, RoundedCornerShape(1.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            iconResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, color = BlackEnd)
        }
    }
}
