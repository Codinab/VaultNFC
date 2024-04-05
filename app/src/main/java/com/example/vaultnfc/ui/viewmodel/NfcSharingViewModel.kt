package com.example.vaultnfc.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.model.serializePasswordItem

class NfcSharingViewModel : ViewModel() {
    fun preparePasswordItemForNfc(item: PasswordItem): String = serializePasswordItem(item)
}
