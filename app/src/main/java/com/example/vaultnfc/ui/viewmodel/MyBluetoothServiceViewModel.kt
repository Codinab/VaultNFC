package com.example.vaultnfc.ui.viewmodel

import android.annotation.SuppressLint
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
import com.example.vaultnfc.data.repository.PasswordSelected.passwordItemSelected
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.model.deserializePasswordItem
import com.example.vaultnfc.model.serializePasswordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * ViewModel to handle Bluetooth service operations including discovering devices,
 * connecting to a device, and managing Bluetooth data transmission.
 *
 * @param application The context used for accessing system Bluetooth services.
 */
class MyBluetoothServiceViewModel(private val application: Application) : ViewModel() {

    // LiveData to communicate with UI components.
    val passwordItemToSave = MutableLiveData<PasswordItem>()

    private val _toastMessages = MutableLiveData<String>()
    val toastMessages: LiveData<String> = _toastMessages

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private var connectedThread: ConnectedThread? = null

    /**
     * Initiates the device discoverability making the device visible to other Bluetooth devices.
     * @param launcher The ActivityResultLauncher to handle the discoverability request.
     */
    fun enableDiscoverability(launcher: ActivityResultLauncher<Intent>) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        launcher.launch(discoverableIntent)
    }

    /**
     * Starts the Bluetooth server to listen for incoming connections.
     */
    @SuppressLint("MissingPermission")
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
                        socket =
                            serverSocket?.accept() // This call blocks until a connection is accepted
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

    /**
     * Connects to a given Bluetooth device.
     * @param device The BluetoothDevice to connect to.
     */
    @SuppressLint("MissingPermission")
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
                    this@MyBluetoothServiceViewModel.discoveredDevices.value =
                        emptyList() // Optional: Clear the list at the start
                }

                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )!!
                    val currentList =
                        this@MyBluetoothServiceViewModel.discoveredDevices.value ?: emptyList()
                    this@MyBluetoothServiceViewModel.discoveredDevices.value = currentList + device
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isDiscovering.value = false
                }
            }
        }
    }

    /**
     * Starts discovering nearby Bluetooth devices.
     */
    @SuppressLint("MissingPermission")
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
        isReceiverRegistered = true

        try {
            bluetoothAdapter?.startDiscovery()
            _toastMessages.postValue("Discovering devices")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling discovery", e)
            _toastMessages.postValue("Couldn't start discovery")
        }
    }

    // Internal functions and class handling Bluetooth data transmission and connection management.
    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        //private val mmBuffer: ByteArray = ByteArray(1024 * 1024)

        override fun run() {
            _isConnected.postValue(true)

            try {
                val reader = mmInStream.bufferedReader() // Create a BufferedReader

                while (!isInterrupted) {
                    val inputString = try {
                        reader.readLine() // Read a line up to the newline character
                    } catch (e: IOException) {
                        Log.d(TAG, "Input stream was disconnected", e)
                        break // Exit the loop if an IOException occurs
                    }

                    // If readLine returns null, it means the end of the stream has been reached
                    if (inputString == null) {
                        Log.d(TAG, "End of stream reached")
                        continue // Exit the loop
                    }

                    val passwordItem = deserializePasswordItem(inputString)

                    passwordItemToSave.postValue(passwordItem)



                    // Use the deserialized passwordItem as needed
                }
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                _toastMessages.postValue("Connection lost")
            }catch (e: Exception) {
                Log.d(TAG, "Error", e)
                _toastMessages.postValue("Error")
            } finally {
                cleanupResources()
            }
        }


        fun write(passwordItem: PasswordItem) {
            try {
                // Serialize your PasswordItem and append a newline character
                val message = serializePasswordItem(passwordItem) + "\n"
                val bytes = message.toByteArray()
                mmOutStream.write(bytes)
                mmOutStream.flush() // Ensure the data is sent immediately
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

    /**
     * Sends a PasswordItem to the connected device.
     */
    fun send() {
        if (isConnected.value == false) {
            Toast.makeText(application, "Not connected to a device", Toast.LENGTH_SHORT).show()
            return
        }
        connectedThread?.write(passwordItemSelected)
    }


    /**
     * Disconnects from the connected Bluetooth device and cleans up resources.
     */
    fun disconnect() {
        try {
            connectedThread?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting", e)
            _toastMessages.postValue("Disconnected from device")
        }
    }


    private var isReceiverRegistered = false
    override fun onCleared() {
        super.onCleared()
        // Check the flag before trying to unregister the receiver
        if (isReceiverRegistered) {
            application.unregisterReceiver(discoveryBroadcastReceiver)
            // Reset the flag as the receiver is now unregistered
            isReceiverRegistered = false
        }
        disconnect() // Ensure Bluetooth connection is closed and resources are cleaned up
    }


    companion object {
        private const val TAG = "MyBluetoothServiceVaultNFC"
    }
}
