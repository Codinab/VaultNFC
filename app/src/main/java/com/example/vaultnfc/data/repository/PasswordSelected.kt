package com.example.vaultnfc.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.vaultnfc.model.PasswordItem

object PasswordSelected {

    val passwordItemSelected = MutableLiveData<PasswordItem>()
}
