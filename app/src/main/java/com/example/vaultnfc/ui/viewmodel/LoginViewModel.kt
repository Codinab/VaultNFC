package com.example.vaultnfc.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vaultnfc.data.repository.SecureStorage
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val loginError = MutableLiveData<String?>()
    val registrationError = MutableLiveData<String?>()
    val isLoggedIn = MutableLiveData<Boolean>()

    // Check if user is already logged in when ViewModel is initialized
    fun checkIfLoggedIn(context: Context) {
        val (email, password) = SecureStorage.getLoginDetails(context)
        if (email != null && password != null) {
            // Automatically log the user in with the saved credentials
            login(email, password, context) {}
        }
    }

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

    private fun checkUserParameters(email: String, password: String, error: MutableLiveData<String?>): Boolean {
        if (email.isBlank() || password.isBlank()) {
            error.postValue("Email or password cannot be empty.")
            return true
        }
        return false
    }
}
