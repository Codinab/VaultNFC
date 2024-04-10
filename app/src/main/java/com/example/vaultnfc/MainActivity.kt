package com.example.vaultnfc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.vaultnfc.ui.AppNavigation
import com.example.vaultnfc.ui.theme.VaultNFCTheme
import com.example.vaultnfc.ui.viewmodel.LoginViewModel
import com.example.vaultnfc.ui.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private lateinit var viewModelProvider: SignInViewModel
    private lateinit var loginViewModel: LoginViewModel

    private var currentLogoutOption: String = "Closing app"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelProvider = ViewModelProvider(this)[SignInViewModel::class.java]
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]



        //Not implemented
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        viewModelProvider.signInSuccess()
                    } catch (e: ApiException) {
                        // The ApiException status code indicates the detailed failure reason.
                        // Handle sign-in failure
                        viewModelProvider.signInFailure()
                    }
                }
            }

        setContent {
            VaultNFCTheme {
                AppNavigation(this.application)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }


    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>



    // Inside your main activity or wherever appropriate
    override fun onStop() {
        super.onStop()

        if (currentLogoutOption == "Closing app") {
            loginViewModel.logout(this)
        }
    }

}