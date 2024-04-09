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
import com.example.vaultnfc.ui.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SignInViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]


        //Not implemented
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        viewModel.signInSuccess()
                    } catch (e: ApiException) {
                        // The ApiException status code indicates the detailed failure reason.
                        // Handle sign-in failure
                        viewModel.signInFailure()
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

    fun signInWithGoogle() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val signInIntent = signInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}


