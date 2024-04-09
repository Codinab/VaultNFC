package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.model.deserializePasswordItem
import com.example.vaultnfc.model.serializePasswordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class MyBluetoothServiceViewModel(private val application: Application) : ViewModel() {


    val passwordItem = MutableLiveData<PasswordItem>()

    private val _toastMessages = MutableLiveData<String>()
    val toastMessages: LiveData<String> = _toastMessages

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private var connectedThread: ConnectedThread? = null


    fun enableDiscoverability(launcher: ActivityResultLauncher<Intent>) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        launcher.launch(discoverableIntent)
    }


    fun startServer() {

        if (inInvalidState()) return

        viewModelScope.launch(Dispatchers.IO) {

            _toastMessages.postValue("Starting server")

            val serverUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
            try {
                bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                    "MyBluetoothService",
                    serverUUID
                ).use { serverSocket ->
                    var socket: BluetoothSocket? = null
                    try {
                        socket = serverSocket?.accept() // This call blocks until a connection is accepted
                        _toastMessages.postValue("Connection accepted")
                    } catch (e: IOException) {
                        Log.e(TAG, "Socket's accept method failed", e)
                    }
                    socket?.also {
                        // Manage the connected socket
                        connectedThread = ConnectedThread(socket)
                        connectedThread!!.start()
                        _toastMessages.postValue("Connected to device")

                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Server socket's listen() method failed", e)
            }
        }
    }

    private fun inInvalidState(): Boolean {
        if (connectedThread != null) {
            Toast.makeText(application, "Already connected to a device", Toast.LENGTH_SHORT).show()
            return true
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(application, "Bluetooth is not available", Toast.LENGTH_SHORT).show()
            return true
        }

        if (!bluetoothAdapter!!.isEnabled) {
            Toast.makeText(application, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
            return true
        }

        if (isDiscovering.value == true) {
            Toast.makeText(application, "Already discovering devices", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }


    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothAdapter?.cancelDiscovery()
            _toastMessages.postValue("Connecting to device")
            try {
                // Standard SerialPortServiceID UUID
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

                var socket: BluetoothSocket? = null
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid)
                    socket.connect()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's create() method failed", e)
                    _toastMessages.postValue("Couldn't create socket")
                }

                socket?.also {
                    connectedThread = ConnectedThread(socket)
                    connectedThread?.start()
                    _toastMessages.postValue("Connected to device")
                }

                // Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception

                // Manage the connected socket (e.g., start thread to manage the connection)
            } catch (e: IOException) {
                // Unable to connect; close the socket and return
            }
        }
    }





    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val _isDiscovering = MutableLiveData<Boolean>(false)
    val isDiscovering: LiveData<Boolean> = _isDiscovering

    val discoveredDevices = MutableLiveData<List<BluetoothDevice>>()

    private val discoveryBroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    _isDiscovering.value = true
                    this@MyBluetoothServiceViewModel.discoveredDevices.value = emptyList() // Optional: Clear the list at the start
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)!!
                    val currentList = this@MyBluetoothServiceViewModel.discoveredDevices.value ?: emptyList()
                    this@MyBluetoothServiceViewModel.discoveredDevices.value = currentList + device
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isDiscovering.value = false
                }
            }
        }
    }

    fun startDiscovery() {
        if (inInvalidState()) return


        this.discoveredDevices.value = emptyList() // Reset the list on new discovery
        // Register for broadcasts when a device is discovered
        _toastMessages.postValue("Starting discovery")

        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        application.registerReceiver(discoveryBroadcastReceiver, filter)

        try {
            bluetoothAdapter?.startDiscovery()
            _toastMessages.postValue("Discovering devices")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling discovery", e)
            _toastMessages.postValue("Couldn't start discovery")
        }
    }

    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            _isConnected.postValue(true)

            try {
                while (!isInterrupted) {
                    val bytes = mmInStream.read(mmBuffer)
                    // Assuming you're using some form of serialization, you'd deserialize here
                    val passwordItemReceived = deserializePasswordItem(mmBuffer.copyOf(bytes).toString())
                    passwordItem.postValue(passwordItemReceived)
                }
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                _toastMessages.postValue("Connection lost: ${e.message}")
            } finally {
                cleanupResources()
            }
        }

        fun write(passwordItem: PasswordItem) {
            try {
                // Serialize your PasswordItem into bytes
                val bytes = serializePasswordItem(passwordItem).toByteArray()
                mmOutStream.write(bytes)
                _toastMessages.postValue("Sent ${passwordItem.title}")
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
                _toastMessages.postValue("Couldn't send data to the other device")
            }
        }

        fun cancel() {
            interrupt()
        }

        private fun cleanupResources() {
            try {
                mmInStream.close()
                mmOutStream.close()
                mmSocket.close()
                connectedThread = null
                _isConnected.postValue(false)

            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connected socket", e)
            }
        }

        override fun interrupt() {
            super.interrupt() // This ensures the thread's interrupted status is set
            cleanupResources() // Close the socket and clean up resources
        }
    }


    class MyBluetoothServiceViewModelFactory(application: Application) :
        ViewModelProvider.Factory {
        private val applicationContext = application

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyBluetoothServiceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MyBluetoothServiceViewModel(applicationContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun send() {
        if(isConnected.value == false) {
            Toast.makeText(application, "Not connected to a device", Toast.LENGTH_SHORT).show()
            return
        }
        if (passwordItem.value == null) {
            Toast.makeText(application, "No password item to send", Toast.LENGTH_SHORT).show()
            return
        }
        connectedThread?.write(passwordItem.value!!)
    }


    /**
     * Disconnects from the connected Bluetooth device and cleans up resources.
     */
    fun disconnect() {
        connectedThread?.cancel()
        _toastMessages.postValue("Disconnected from device")
    }

    override fun onCleared() {
        super.onCleared()
        application.unregisterReceiver(discoveryBroadcastReceiver)
        disconnect() // Ensure Bluetooth connection is closed and resources are cleaned up
    }


    companion object {
        private const val TAG = "MyBluetoothServiceVM"
    }
}
