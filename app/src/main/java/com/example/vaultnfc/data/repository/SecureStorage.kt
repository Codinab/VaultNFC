package com.example.vaultnfc.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {

    private const val FILE_NAME = "encrypted_shared_prefs"
    private const val EMAIL_KEY = "email"
    private const val PASSWORD_KEY = "password"

    private fun getEncryptedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveLoginDetails(context: Context, email: String, password: String) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().putString(EMAIL_KEY, email).putString(PASSWORD_KEY, password).apply()
    }

    fun getLoginDetails(context: Context): Pair<String?, String?> {
        val prefs = getEncryptedPreferences(context)
        val email = prefs.getString(EMAIL_KEY, null)
        val password = prefs.getString(PASSWORD_KEY, null)
        return Pair(email, password)
    }
}
