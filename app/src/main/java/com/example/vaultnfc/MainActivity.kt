package com.example.vaultnfc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.vaultnfc.ui.AppNavigation
import com.example.vaultnfc.ui.theme.VaultNFCTheme
import com.example.vaultnfc.ui.viewmodel.LoginViewModel
import com.example.vaultnfc.ui.viewmodel.PermissionViewModel
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel
import com.example.vaultnfc.util.EventObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * The main activity for the application, serving as the entry point.
 *
 * This activity is responsible for initializing the application's theme, navigation, and view models.
 * It observes the selected logout option from the settings and handles the logout process based on user preferences.
 */
class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var permissionViewModel: PermissionViewModel



    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            permissionViewModel.handlePermissionDenied()
        }
    }

    /**
     * Initializes the activity, setting up view models, theme, and app navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModel initialization
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]


        // Observing logout timer option changes
        settingsViewModel.logoutTimerOption.asLiveData().observe(this) { option ->
            currentLogoutOption = option
        }

        createNotificationChannels()

        handeTokenSaving()

        setFirestoreCacheSettings()


        // Setting content view with Compose UI
        setContent {
            VaultNFCTheme {
                AppNavigation(application)
            }
        }
    }

    private fun handeTokenSaving() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            saveTokenToFirestore(token)
        }
    }

    private fun setFirestoreCacheSettings() {
        val firestore = FirebaseFirestore.getInstance()
        val cacheSettings = PersistentCacheSettings.newBuilder()
            .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cacheSettings)
            .build()

        firestore.firestoreSettings = settings
    }

    private fun saveTokenToFirestore(token: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        if (token != null) {
            db.collection("users").document(userId).collection("tokens").document(token).set(mapOf("token" to token))
        }
    }


    private fun createNotificationChannels() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Password update notification channel
        val updateChannelId = getString(R.string.password_update_channel_id)
        val updateChannelName = getString(R.string.password_update_channel_name)
        val updateChannelDescription = getString(R.string.password_update_channel_description)
        val updateChannel = NotificationChannel(updateChannelId, updateChannelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = updateChannelDescription
        }

        // Password creation notification channel
        val creationChannelId = getString(R.string.password_creation_channel_id)
        val creationChannelName = getString(R.string.password_creation_channel_name)
        val creationChannelDescription = getString(R.string.password_creation_channel_description)
        val creationChannel = NotificationChannel(creationChannelId, creationChannelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = creationChannelDescription
        }

        // Password deletion notification channel
        val deletionChannelId = getString(R.string.password_deletion_channel_id)
        val deletionChannelName = getString(R.string.password_deletion_channel_name)
        val deletionChannelDescription = getString(R.string.password_deletion_channel_description)
        val deletionChannel = NotificationChannel(deletionChannelId, deletionChannelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = deletionChannelDescription
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            observePermissionRequests()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionViewModel.requestNotificationPermissions()
        }


        // Register all channels with the system
        notificationManager.createNotificationChannel(updateChannel)
        notificationManager.createNotificationChannel(creationChannel)
        notificationManager.createNotificationChannel(deletionChannel)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun observePermissionRequests() {
        permissionViewModel.notificationPermissionRequestEvent.observe(this, EventObserver { _ ->
            val isPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!isPermissionGranted) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        })
    }

    /**
     * Called when a new intent is received.
     *
     * Use this method to handle new intents sent to the activity while it's running.
     * This can occur when the activity is already running in the foreground or if it's being re-launched
     * with a new intent after being destroyed.
     *
     * @param intent The new intent that was started for the activity.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    private var currentLogoutOption = SettingsViewModel.TIMEOUT_MODE[1]

    /**
     * Called when the activity is about to stop.
     *
     * It checks the current logout option and initiates the logout process if
     * the option is set to "Closing app".
     */
    override fun onStop() {
        super.onStop()

        if (currentLogoutOption == SettingsViewModel.TIMEOUT_MODE[1]) {
            loginViewModel.logout()
        }
    }


}