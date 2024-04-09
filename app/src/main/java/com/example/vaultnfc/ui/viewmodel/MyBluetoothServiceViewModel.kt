package com.example.vaultnfc.ui.viewmodel

import PasswordsViewModel
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



    private val _readMessages = MutableLiveData<ByteArray>()
    val readMessages: LiveData<ByteArray> = _readMessages

    private val _writeMessages = MutableLiveData<String>()
    val writeMessages: LiveData<String> = _writeMessages

    private val _toastMessages = MutableLiveData<String>()
    val toastMessages: LiveData<String> = _toastMessages

    private var connectedThread: ConnectedThread? = null


    fun enableDiscoverability(launcher: ActivityResultLauncher<Intent>) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        launcher.launch(discoverableIntent)
    }


    fun startServer() {
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
                        val passwordsViewModel = PasswordsViewModel()
                        connectedThread = ConnectedThread(socket, passwordsViewModel)
                        connectedThread!!.start()
                        _toastMessages.postValue("Connected to device")

                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Server socket's listen() method failed", e)
            }
        }
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
                    val passwordsViewModel = PasswordsViewModel()
                    connectedThread = ConnectedThread(socket, passwordsViewModel)
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

    private val _isDiscovering = MutableLiveData<Boolean>()

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

    inner class ConnectedThread(private val mmSocket: BluetoothSocket, private val passwordsViewModel: PasswordsViewModel) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                numBytes = try {
                    // Read from the InputStream
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI
                val readMessage = mmBuffer.toString()
                val passwordItem = deserializePasswordItem(readMessage)

                passwordsViewModel.addPassword(
                    passwordItem.title,
                    passwordItem.username,
                    passwordItem.encryptedPassword,
                    passwordItem.uri,
                    passwordItem.notes
                )
                // Now you can use passwordItem as needed
            }
        }


        fun write(bytes: ByteArray) {
            val item = PasswordItem(
                title = "Title",
                username = "Username",
                encryptedPassword = "EncryptedPassword",
                uri = "Uri",
                notes = "Notes"
            )
            val json = serializePasswordItem(item)
            val bytes1 = json.toByteArray(Charsets.UTF_8)
            try {
                mmOutStream.write(bytes1)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
                _toastMessages.postValue("Couldn't send data to the other device")
            }
        }


        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
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

    fun write(bytes: ByteArray) {
        connectedThread?.write(bytes)
    }

    private fun cancel() {
        connectedThread?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        application.unregisterReceiver(discoveryBroadcastReceiver)
        cancel() // Call your cancel method to close the Bluetooth connection
    }


    companion object {
        private const val TAG = "MyBluetoothServiceVM"
    }
}
