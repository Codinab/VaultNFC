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

    /**
     * Registers a new user with an email and password.
     *
     * @param email The email address to be used for registration.
     * @param password The password for the new account.
     * @param context The context used for saving login details securely upon successful registration.
     * @param onSuccess A callback to be invoked upon successful registration.
     */
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
