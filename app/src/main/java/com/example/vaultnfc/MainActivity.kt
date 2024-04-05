package com.example.vaultnfc

import PasswordGeneratorViewModel
import PasswordsViewModel
import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
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

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    // Sign in was successful, notify ViewModel or handle as needed
                    // Assuming you have a method or a way to notify your ViewModel
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
                AppNavigation(baseContext)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Check if the intent is an NFC intent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                // Process the messages
                // For example, parse the first message
                val messages = rawMessages.map { it as NdefMessage }
                val payload = messages[0].records[0].payload
                // Assuming payload contains a serialized PasswordItem
                val passwordItemJson = String(payload)
                // Pass the JSON string to your ViewModel or directly to the Composable
            }
        }
    }



    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    fun signInWithGoogle() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val signInIntent = signInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}


