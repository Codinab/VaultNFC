package com.example.vaultnfc.ui.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context.BLUETOOTH_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vaultnfc.util.Event

class PermissionViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _bluetoothEnableEvent = MutableLiveData<Event<Unit>>()
    val bluetoothEnableEvent: LiveData<Event<Unit>> = _bluetoothEnableEvent

    private val _bluetoothPermissionRequestEvent = MutableLiveData<Event<Array<String>>>()
    val bluetoothPermissionRequestEvent: LiveData<Event<Array<String>>> =
        _bluetoothPermissionRequestEvent

    private val _bluetoothStateChangeEvent = MutableLiveData<Event<Int>>()
    val bluetoothStateChangeEvent: LiveData<Event<Int>> = _bluetoothStateChangeEvent

    private val _gpsEnabledEvent = MutableLiveData<Event<Boolean>>()
    val gpsEnabledEvent: LiveData<Event<Boolean>> = _gpsEnabledEvent


    private val _navigateToSettingsEvent = MutableLiveData<Event<Unit>>()
    val navigateToSettingsEvent: LiveData<Event<Unit>> = _navigateToSettingsEvent


    /**
     * Lazily initialized BluetoothAdapter instance.
     */
    val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    /**
     * Checks the device's Bluetooth status and triggers an event to enable Bluetooth if it is disabled.
     */
    fun checkAndEnableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter!!.isEnabled) {
            _bluetoothEnableEvent.value = Event(Unit)
        }
    }


    /**
     * Requests necessary permissions for Bluetooth operation on devices running Android 12 (API level 31) or higher.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestBluetoothPermissions() {
        _bluetoothPermissionRequestEvent.value = Event(
            arrayOf(
                BLUETOOTH,
                BLUETOOTH_ADMIN,
                ACCESS_FINE_LOCATION,
                BLUETOOTH_CONNECT,
                BLUETOOTH_SCAN
            )
        )
    }

    /**
     * Handles the scenario when permissions are denied by the user.
     *
     * This function sets an event to navigate to the app settings, allowing the user to manually enable permissions.
     */
    fun handlePermissionDenied() {
        _navigateToSettingsEvent.value = Event(Unit)
    }
}

