package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.SecureStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

class MasterKeyViewModel(application: Application) : AndroidViewModel(application) {

    private val maxAttempts = 5
    private val attemptWindowMillis = 60000L  // 1 minute
    private val attemptLog = ConcurrentHashMap<Long, Int>()

    private val _blockUser = MutableStateFlow(false)
    val blockUser: StateFlow<Boolean> = _blockUser.asStateFlow()

    val masterKeyError = MutableLiveData<String?>()
    val isMasterKeySet = MutableLiveData<Boolean>()

    fun saveMasterKey(masterKey: String) = viewModelScope.launch {
        if (isBlocked()) {
            masterKeyError.postValue("Too many attempts. Please wait before trying again.")
        } else {
            logAttempt()
            SecureStorage.saveMasterKey(getApplication(), masterKey)
            isMasterKeySet.postValue(true)
            masterKeyError.postValue(null)
        }
    }

    fun clearMasterKey() = viewModelScope.launch {
        SecureStorage.clearMasterKey(getApplication())
        isMasterKeySet.postValue(false)
    }

    private fun logAttempt() {
        val currentTime = System.currentTimeMillis()
        attemptLog[currentTime] = (attemptLog[currentTime] ?: 0) + 1
        cleanupOldAttempts()
        if (getRecentAttemptCount() > maxAttempts) {
            _blockUser.value = true
        }
    }

    private fun cleanupOldAttempts() {
        val cutoffTime = System.currentTimeMillis() - attemptWindowMillis
        attemptLog.keys.removeIf { it < cutoffTime }
    }

    private fun getRecentAttemptCount(): Int {
        cleanupOldAttempts()
        return attemptLog.values.sum()
    }

    private fun isBlocked(): Boolean {
        return _blockUser.value
    }
}
