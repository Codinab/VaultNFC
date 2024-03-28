package com.example.vaultnfc.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
    private val _signInStatus = MutableLiveData<Boolean>()
    val signInStatus: LiveData<Boolean> get() = _signInStatus

    fun signInSuccess() {
        _signInStatus.value = true
    }

    fun signInFailure() {
        _signInStatus.value = false
    }
}
