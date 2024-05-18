package com.example.vaultnfc.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.pow

class MasterKeyViewModel(application: Application) : AndroidViewModel(application) {

    private val maxHourlyAttempts = 4 // 5 attempts in an hour
    private val maxDailyAttempts = 9 // 10 attempts in a day
    private val hourMillis = 3600000L // 1 hour
    private val dayMillis = 24 * 60 * 60 * 1000L // 1 day
    private val hourlyAttemptLog: MutableSet<Long> = SecureStorage.getHourlyAttemptLog(application)
    private val dailyAttemptLog: MutableSet<Long> = SecureStorage.getDailyAttemptLog(application)

    private val initialBlockTimeMillis =
        5 * 60 * 500L // 5 minutes in milliseconds for the first block

    private val _blockUser = MutableStateFlow(false)
    val blockUser: StateFlow<Boolean> = _blockUser.asStateFlow()

    private val _blockEndTime = MutableStateFlow<Long?>(null)
    val blockEndTime: StateFlow<Long?> = _blockEndTime.asStateFlow()

    val masterKeyError = MutableLiveData<String?>()
    val isMasterKeySet = MutableLiveData<Boolean>()

    init {
        isMasterKeySet.value = SecureStorage.getMasterKey(application) != null
        updateBlockState(application)
    }

    fun updateMasterKeySet() {
        isMasterKeySet.value = SecureStorage.getMasterKey(getApplication()) != null
    }

    fun saveMasterKey(masterKey: String) = viewModelScope.launch {
        if (isBlocked()) {
            masterKeyError.postValue("Blocking more attempts. Please wait.")
        } else {
            logAttempt(getApplication())
            SecureStorage.saveMasterKey(getApplication(), masterKey)
            updateMasterKeySet()
        }
    }

    fun canAttempt() = !isBlocked()

    fun getMasterKey() = SecureStorage.getMasterKey(getApplication())

    fun clearMasterKey() = viewModelScope.launch {
        SecureStorage.clearMasterKey(getApplication())
        isMasterKeySet.postValue(false)
    }

    private fun logAttempt(context: Context) {
        val currentTime = System.currentTimeMillis()
        hourlyAttemptLog.add(currentTime)
        dailyAttemptLog.add(currentTime)
        cleanupOldAttempts(context)
        updateBlockState(context)
    }

    private fun updateBlockState(context: Context) {
        val recentHourlyAttempts = getRecentHourlyAttemptCount()
        val recentDailyAttempts = getRecentDailyAttemptCount()

        if (recentDailyAttempts > maxDailyAttempts) {
            // Block for the rest of the day
            _blockEndTime.value = System.currentTimeMillis() + dayMillis
            _blockUser.value = true
        } else if (recentHourlyAttempts > maxHourlyAttempts) {
            // Block based on the number of hourly attempts
            val blockTime =
                initialBlockTimeMillis * (2.0.pow((recentHourlyAttempts - maxHourlyAttempts).toDouble())
                    .toLong())
            _blockEndTime.value = System.currentTimeMillis() + blockTime
            _blockUser.value = true
        } else {
            _blockUser.value = false
            _blockEndTime.value = null
        }

        // Save the updated logs to SecureStorage
        SecureStorage.saveHourlyAttemptLog(context, hourlyAttemptLog)
        SecureStorage.saveDailyAttemptLog(context, dailyAttemptLog)
    }

    private fun cleanupOldAttempts(context: Context) {
        val cutoffHourTime = System.currentTimeMillis() - hourMillis
        hourlyAttemptLog.removeIf { it < cutoffHourTime }

        val cutoffDayTime = System.currentTimeMillis() - dayMillis
        dailyAttemptLog.removeIf { it < cutoffDayTime }

        // Save the cleaned logs to SecureStorage
        SecureStorage.saveHourlyAttemptLog(context, hourlyAttemptLog)
        SecureStorage.saveDailyAttemptLog(context, dailyAttemptLog)
    }

    private fun getRecentHourlyAttemptCount(): Int {
        cleanupOldAttempts(getApplication())
        return hourlyAttemptLog.size
    }

    private fun getRecentDailyAttemptCount(): Int {
        cleanupOldAttempts(getApplication())
        return dailyAttemptLog.size
    }

    private fun isBlocked(): Boolean {
        val currentTime = System.currentTimeMillis()
        val endTime = _blockEndTime.value
        if (endTime != null && currentTime < endTime) {
            return true
        }
        // Unblock the user if the current time has passed the block end time
        _blockUser.value = false
        _blockEndTime.value = null
        return false
    }

    fun getBlockEndTimeFormatted(): String? {
        val endTime = _blockEndTime.value ?: return null
        val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(endTime)
    }

    fun getTimeUntilUnblockedFormatted(): String {
        val currentTime = System.currentTimeMillis()
        val endTime = _blockEndTime.value ?: return "00:00:00"
        val remainingTime = if (currentTime < endTime) endTime - currentTime else 0

        val hours = remainingTime / (1000 * 60 * 60)
        val minutes = (remainingTime / (1000 * 60)) % 60
        val seconds = (remainingTime / 1000) % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    //Used for debugging, delete before production
    fun clearLoginAttempts() {
        hourlyAttemptLog.clear()
        dailyAttemptLog.clear()

        SecureStorage.saveHourlyAttemptLog(getApplication(), hourlyAttemptLog)
        SecureStorage.saveDailyAttemptLog(getApplication(), dailyAttemptLog)

        _blockUser.value = false
        _blockEndTime.value = null
    }
}
