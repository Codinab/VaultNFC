package com.example.vaultnfc.util

import android.net.wifi.p2p.WifiP2pManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vaultnfc.ui.viewmodel.WifiDirectViewModel

class WifiDirectViewModelFactory(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiDirectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiDirectViewModel(manager, channel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
