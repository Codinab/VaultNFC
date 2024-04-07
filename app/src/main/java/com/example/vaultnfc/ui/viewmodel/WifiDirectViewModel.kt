package com.example.vaultnfc.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.net.MacAddress
import android.net.NetworkCapabilities
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class WifiDirectViewModel(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
) : ViewModel() {

    val isWifiP2pEnabled = MutableLiveData<Boolean>()
    val peers = MutableLiveData<List<WifiP2pDevice>>()
    val connectionStatus = MutableLiveData<String>()
    val receivedMessage = MutableLiveData<String>()
    val thisDevice = MutableLiveData<WifiP2pDevice>()



    private val peerListListener = WifiP2pManager.PeerListListener { peerList: WifiP2pDeviceList? ->
        peers.postValue(peerList?.deviceList?.toList())
    }
    init {
        // Note: Consider calling discoverPeers from an Activity or Fragment with proper checks
    }

    // Member variable in WifiDirectViewModel
    private var wifiP2pInfo: WifiP2pInfo? = null

    // Updated connectionInfoListener
    private val connectionInfoListener = WifiP2pManager.ConnectionInfoListener { info: WifiP2pInfo ->
        wifiP2pInfo = info
        if (info.groupFormed && info.isGroupOwner) {
            // The device is the group owner; start server socket
            connectionStatus.postValue("Connected as Group Owner")
            startServerSocket()
        } else if (info.groupFormed) {
            // The device is a client; start client socket
            connectionStatus.postValue("Connected as Peer")
        }
    }

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var socketThread: Thread? = null

    fun sendText(message: String) {
        val info = wifiP2pInfo // Use the stored WifiP2pInfo
        if (info == null || !info.groupFormed) {
            connectionStatus.postValue("Not connected")
            return
        }

        Thread {
            try {
                if (info.isGroupOwner) {
                    // This device is the group owner; no need to initiate a connection here.
                    // The server socket should be listening for incoming connections.
                } else {
                    // This device is a client; connect to the group owner's IP and send the message.
                    val groupOwnerAddress = info.groupOwnerAddress.hostAddress
                    clientSocket = Socket(groupOwnerAddress, 8988) // Use the agreed-upon port.
                    val outputStream = clientSocket!!.getOutputStream()
                    outputStream.write((message + "\n").toByteArray(Charsets.UTF_8))
                    outputStream.close()
                    clientSocket!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    // Placeholder for starting a server socket
    private fun startServerSocket() {
        viewModelScope.launch {
            try {
                serverSocket = ServerSocket(8988) // Listen on an agreed-upon port.
                while (true) {
                    val socket = serverSocket!!.accept()
                    // Handle the incoming connection and read messages.
                    // Update receivedMessage LiveData here.
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    fun updateWifiP2pStatus(isEnabled: Boolean) {
        isWifiP2pEnabled.postValue(isEnabled)
    }

    fun updateThisDevice(device: WifiP2pDevice) {
        thisDevice.postValue(device)
    }

    @SuppressLint("MissingPermission")
    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                connectionStatus.postValue("Discovery Started")
            }

            override fun onFailure(reason: Int) {
                connectionStatus.postValue("Discovery Failed: $reason")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    fun connectToDevice(device: WifiP2pDevice) {
        val configBuilder = WifiP2pConfig.Builder()
        val macAddress = MacAddress.fromString(device.deviceAddress)
        configBuilder.setDeviceAddress(macAddress)
        val config = configBuilder.build()

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Connection initiation was successful. Actual connection
                // will be confirmed through WIFI_P2P_CONNECTION_CHANGED_ACTION.
                connectionStatus.postValue("Connecting to ${device.deviceName}")
            }

            override fun onFailure(reason: Int) {
                // Connection initiation failed. Handle the error.
                // Reason codes include P2P_UNSUPPORTED, ERROR, or BUSY.
                connectionStatus.postValue("Connection Failed: $reason")
            }
        })
    }


    fun requestConnectionInfo() {
        manager.requestConnectionInfo(channel, connectionInfoListener)
    }

    @SuppressLint("MissingPermission")
    fun peersChanged() {
        manager.requestPeers(channel, peerListListener)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun connectionChanged(intent: Intent) {
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == intent.action) {
            val networkCapabilities = intent.getParcelableExtra<NetworkCapabilities>(
                WifiP2pManager.EXTRA_NETWORK_INFO,
                NetworkCapabilities::class.java
            )

            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, connectionInfoListener)
            } else {
                // It's a disconnect
                connectionStatus.postValue("Disconnected")
            }
        }
    }

}




