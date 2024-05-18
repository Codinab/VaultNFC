package com.example.vaultnfc.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vaultnfc.data.repository.SecureStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val loginError = MutableLiveData<String?>()
    val registrationError = MutableLiveData<String?>()
    val isLoggedIn = MutableLiveData<Boolean>()

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (checkUserParameters(email, password, loginError)) return

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SecureStorage.saveLoginDetails(context, email, password)
                loginError.postValue(null)
                isLoggedIn.postValue(true)
                onSuccess()
            } else {
                loginError.postValue(task.exception?.message ?: "Login failed")
            }
        }
    }

    fun logout(context: Context) {
        SecureStorage.clearLoginDetails(context)
        auth.signOut()
        isLoggedIn.postValue(false)
    }

    fun register(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (checkUserParameters(email, password, registrationError)) return

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SecureStorage.saveLoginDetails(context, email, password)
                registrationError.postValue(null)
                isLoggedIn.postValue(true)
                onSuccess()
            } else {
                registrationError.postValue(task.exception?.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String, context: Context) {
        if (email.isBlank()) {
            loginError.postValue("Please enter your email address to reset your password.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset link sent to your email address", Toast.LENGTH_LONG).show()
                } else {
                    loginError.postValue(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    private fun checkUserParameters(email: String, password: String, error: MutableLiveData<String?>): Boolean {
        if (email.isBlank() || password.isBlank()) {
            error.postValue("Email or password cannot be empty.")
            return true
        }
        return false
    }

    fun loginWithGitHub(context: Context, onSuccess: () -> Unit) {
        val provider = OAuthProvider.newBuilder("github.com")

        // Specify any additional scopes you need for the provider
        provider.scopes = listOf("user:email")

        // Check if there are already pending results
        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask.addOnCompleteListener { task ->
                handleGitHubSignInResult(task, context, onSuccess)
            }
        } else {
            auth.startActivityForSignInWithProvider(context as Activity, provider.build())
                .addOnCompleteListener { task ->
                    handleGitHubSignInResult(task, context, onSuccess)
                }
        }
    }

    private fun handleGitHubSignInResult(task: Task<AuthResult>, context: Context, onSuccess: () -> Unit) {
        if (task.isSuccessful) {
            val user = task.result?.user
            if (user != null) {
                SecureStorage.saveLoginDetails(context, user.email ?: "", "")
                loginError.postValue(null)
                isLoggedIn.postValue(true)
                onSuccess()
            } else {
                loginError.postValue("GitHub login failed: User is null")
            }
        } else {
            loginError.postValue(task.exception?.message ?: "GitHub login failed")
        }
    }
}
