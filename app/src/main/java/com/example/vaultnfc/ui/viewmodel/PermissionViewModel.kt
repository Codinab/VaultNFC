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
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
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


    val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    fun checkAndEnableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter!!.isEnabled) {
            _bluetoothEnableEvent.value = Event(Unit)
        }
    }

    fun checkGpsStatus() {
        val locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        _gpsEnabledEvent.value = Event(isGpsEnabled)
    }

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

    override fun onCleared() {
        super.onCleared()
        // Unregister receiver to avoid memory leaks
    }


    fun handlePermissionDenied() {
        // This method would be called if permissions are denied.
        // It sets the event to navigate to the app settings.
        _navigateToSettingsEvent.value = Event(Unit)
    }

}

