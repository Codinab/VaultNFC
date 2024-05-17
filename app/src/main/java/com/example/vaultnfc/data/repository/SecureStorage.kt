package com.example.vaultnfc.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Class responsible for securely storing login details and the master key using EncryptedSharedPreferences.
 */
object SecureStorage {
    private const val FILE_NAME = "encrypted_shared_prefs"

    private const val EMAIL_KEY = "email"
    private const val PASSWORD_KEY = "password"
    private const val MASTER_KEY = "master_key"

    // Enum to manage setting keys as a clean way to handle the constants.
    enum class SettingsKey {
        LENGTH_KEY, NUMBERS_PROBABILITY_KEY, SYMBOLS_PROBABILITY_KEY, UPPERCASE_PROBABILITY_KEY, LOWERCASE_PROBABILITY_KEY
    }

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
     * Clears the login details and master key stored in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     */
    fun clearAllDetails(context: Context) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().remove(EMAIL_KEY).remove(PASSWORD_KEY).remove(MASTER_KEY).apply()
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

    /**
     * Saves the generator settings securely in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @param length The desired length of the password.
     * @param numbersProb Probability of including numbers.
     * @param symbolsProb Probability of including symbols.
     * @param uppercaseProb Probability of including uppercase letters.
     * @param lowercaseProb Probability of including lowercase letters.
     */
    fun saveGeneratorSettings(
        context: Context,
        length: Int,
        numbersProb: Int,
        symbolsProb: Int,
        uppercaseProb: Int,
        lowercaseProb: Int,
    ) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().apply {
            putInt(SettingsKey.LENGTH_KEY.name, length)
            putInt(SettingsKey.NUMBERS_PROBABILITY_KEY.name, numbersProb)
            putInt(SettingsKey.SYMBOLS_PROBABILITY_KEY.name, symbolsProb)
            putInt(SettingsKey.UPPERCASE_PROBABILITY_KEY.name, uppercaseProb)
            putInt(SettingsKey.LOWERCASE_PROBABILITY_KEY.name, lowercaseProb)
            apply()
        }
    }

    /**
     * Retrieves the generator settings securely from the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @return A Map containing the generator settings retrieved from the EncryptedSharedPreferences.
     */
    fun getGeneratorSettings(context: Context): Map<String, Int> {
        val prefs = getEncryptedPreferences(context)
        return mapOf(
            SettingsKey.LENGTH_KEY.name to prefs.getInt(SettingsKey.LENGTH_KEY.name, 12),
            SettingsKey.NUMBERS_PROBABILITY_KEY.name to prefs.getInt(
                SettingsKey.NUMBERS_PROBABILITY_KEY.name, 1
            ),
            SettingsKey.SYMBOLS_PROBABILITY_KEY.name to prefs.getInt(
                SettingsKey.SYMBOLS_PROBABILITY_KEY.name, 1
            ),
            SettingsKey.UPPERCASE_PROBABILITY_KEY.name to prefs.getInt(
                SettingsKey.UPPERCASE_PROBABILITY_KEY.name, 1
            ),
            SettingsKey.LOWERCASE_PROBABILITY_KEY.name to prefs.getInt(
                SettingsKey.LOWERCASE_PROBABILITY_KEY.name, 1
            )
        )
    }

    /**
     * Clears the generator settings stored in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @param key The key to be cleared.
     */
    fun clearSetting(context: Context, key: SettingsKey): Int {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().remove(key.name).apply()
        return getGeneratorSettings(context)[key.name] ?: 0
    }

    /**
     * Saves the master key securely in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @param masterKey The master key to be saved.
     */
    fun saveMasterKey(context: Context, masterKey: String) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().putString(MASTER_KEY, masterKey).apply()
    }

    /**
     * Retrieves the master key securely from the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     * @return The master key retrieved from the EncryptedSharedPreferences.
     */
    fun getMasterKey(context: Context): String? {
        val prefs = getEncryptedPreferences(context)
        return prefs.getString(MASTER_KEY, null)
    }

    /**
     * Clears the master key stored in the EncryptedSharedPreferences.
     *
     * @param context The context used to access the EncryptedSharedPreferences.
     */
    fun clearMasterKey(context: Context) {
        val prefs = getEncryptedPreferences(context)
        prefs.edit().remove(MASTER_KEY).apply()
    }
}
