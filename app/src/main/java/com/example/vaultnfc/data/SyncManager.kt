package com.example.vaultnfc.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.vaultnfc.data.preferences.DataUsagePreference
import com.example.vaultnfc.data.preferences.SyncPreference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Class responsible for managing the synchronization settings of the application.
 *
 * @property application The application context used for initializing preferences.
 */
class SyncManager(application: Application) {
    private val firestore = FirebaseFirestore.getInstance()
    private val syncPreference = SyncPreference(application)
    private val dataUsagePreference = DataUsagePreference(application)
    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun isConnectedToWiFi(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun isConnectedToMobileData(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    /**
     * Updates the synchronization settings based on the current network state and user preferences.
     */
    fun updateSyncSettings() {
        val syncEnabled = runBlocking {
            syncPreference.syncWithCloud.first()
        }

        val useMobileData = runBlocking {
            dataUsagePreference.useMobileData.first()
        }

        val networkAllowed = if (useMobileData) {
            isConnectedToWiFi() || isConnectedToMobileData()
        } else {
            isConnectedToWiFi()
        }

        if (syncEnabled && networkAllowed) {
            firestore.enableNetwork()
        } else {
            firestore.disableNetwork()
        }
    }
}
