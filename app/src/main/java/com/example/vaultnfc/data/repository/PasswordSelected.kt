package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.PasswordItem

/**
 * Simplified singleton object to store the selected password item.
 */
object PasswordSelected {
    var passwordItemSelected = PasswordItem()
}
