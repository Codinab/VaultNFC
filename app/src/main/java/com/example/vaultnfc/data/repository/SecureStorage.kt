package com.example.vaultnfc.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Class responsible for securely storing login details using EncryptedSharedPreferences.
 */
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

    /**
     * Clears the login details stored in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     */
    fun clearLoginDetails(context: Context) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().remove(EMAIL_KEY).remove(PASSWORD_KEY).apply()
    }

    /**
     * Saves the login details securely in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @param email The email address to be saved.
     * @param password The password to be saved.
     */
    fun saveLoginDetails(context: Context, email: String, password: String) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().putString(EMAIL_KEY, email).putString(PASSWORD_KEY, password).apply()
    }

    /**
     * Retrieves the login details securely from the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @return A Pair containing the email and password retrieved from the EncryptedSharedPreferences.
     */
    fun getLoginDetails(context: Context): Pair<String?, String?> {
        val prefs = getEncryptedPreferences(context)
        val email = prefs.getString(EMAIL_KEY, null)
        val password = prefs.getString(PASSWORD_KEY, null)
        return Pair(email, password)
    }
}
