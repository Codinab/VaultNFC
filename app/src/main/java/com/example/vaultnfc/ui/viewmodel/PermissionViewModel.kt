package com.example.vaultnfc.ui.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context.BLUETOOTH_SERVICE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vaultnfc.util.Event

class PermissionViewModel(application: Application) : AndroidViewModel(application) {
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
    }
}

