package com.example.vaultnfc.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vaultnfc.model.PasswordItem

class PasswordMessagingViewmodel: ViewModel() {

    val passwordItem = MutableLiveData<PasswordItem>()

}