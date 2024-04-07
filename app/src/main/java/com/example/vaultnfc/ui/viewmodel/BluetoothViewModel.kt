package com.example.vaultnfc.ui.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vaultnfc.util.Event

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val _bluetoothEnableEvent = MutableLiveData<Event<Unit>>()
    val bluetoothEnableEvent: LiveData<Event<Unit>> = _bluetoothEnableEvent

    private val _bluetoothPermissionRequestEvent = MutableLiveData<Event<Array<String>>>()
    val bluetoothPermissionRequestEvent: LiveData<Event<Array<String>>> = _bluetoothPermissionRequestEvent

    private val _bluetoothStateChangeEvent = MutableLiveData<Event<Int>>()
    val bluetoothStateChangeEvent: LiveData<Event<Int>> = _bluetoothStateChangeEvent


    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent?.action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                _bluetoothStateChangeEvent.postValue(Event(state))
            }
        }
    }

    init {
        // Register for Bluetooth state changes
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        application.registerReceiver(bluetoothStateReceiver, filter)
    }

    fun checkAndEnableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter!!.isEnabled) {
            _bluetoothEnableEvent.value = Event(Unit)
        }
    }

    fun requestBluetoothPermissions() {
        _bluetoothPermissionRequestEvent.value = Event(arrayOf(
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            ACCESS_FINE_LOCATION
        ))
    }

    override fun onCleared() {
        super.onCleared()
        // Unregister receiver to avoid memory leaks
        getApplication<Application>().unregisterReceiver(bluetoothStateReceiver)
    }
}

