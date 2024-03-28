package com.example.vaultnfc.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginViewModel : ViewModel() {
    fun signInWithGoogle(context: Context, onResult: (Boolean) -> Unit) {
        val signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val signInIntent = signInClient.signInIntent
        // Start the sign-in intent and handle the result in onActivityResult
        // Call onResult(true) if successful, onResult(false) if not
    }
}
