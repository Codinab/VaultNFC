package com.example.vaultnfc.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val loginError = MutableLiveData<String?>()
    val registrationError = MutableLiveData<String?>()
    val isLoggedInMutable = MutableLiveData<Boolean>()


    init {
        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            isLoggedInMutable.postValue(true)
        } else {
            isLoggedInMutable.postValue(false)
        }
    }

    fun isAuth(): Boolean {
        return auth.currentUser != null
    }

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (checkUserParameters(email, password, loginError)) return

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginError.postValue(null)
                isLoggedInMutable.postValue(true)
                onSuccess()
            } else {
                loginError.postValue(task.exception?.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        auth.signOut()
        isLoggedInMutable.postValue(false)
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        if (checkUserParameters(email, password, registrationError)) return

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                registrationError.postValue(null)
                isLoggedInMutable.postValue(true)
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

    fun loginWithGitHub(activity: Activity, onSuccess: () -> Unit) {
        val provider = OAuthProvider.newBuilder("github.com")

        // Check for existing accounts with the provider
        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnCompleteListener { task ->
                    handleSignInResult(task, onSuccess)
                }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnCompleteListener { task ->
                    handleSignInResult(task, onSuccess)
                }
        }
    }

    private fun handleSignInResult(task: Task<AuthResult>, onSuccess: () -> Unit) {
        if (task.isSuccessful) {
            val user = task.result?.user
            if (user != null) {
                loginError.postValue(null)
                isLoggedInMutable.postValue(true)
                onSuccess()
            } else {
                loginError.postValue("GitHub login failed: User is null")
            }
        } else {
            loginError.postValue(task.exception?.message ?: "GitHub login failed")
        }
    }
}
