package com.example.vaultnfc

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.vaultnfc.ui.AppNavigation
import com.example.vaultnfc.ui.theme.VaultNFCTheme
import com.example.vaultnfc.ui.viewmodel.SignInViewModel
import com.example.vaultnfc.ui.viewmodel.WifiDirectViewModel
import com.example.vaultnfc.util.WifiDirectBroadcastReceiver
import com.example.vaultnfc.util.WifiDirectViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private lateinit var signInViewModel: SignInViewModel

    // Wifi Direct
    private lateinit var wifiDirectViewModel: WifiDirectViewModel
    private lateinit var receiver: WifiDirectBroadcastReceiver
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]


        //Not implemented
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        signInViewModel.signInSuccess()
                    } catch (e: ApiException) {
                        // The ApiException status code indicates the detailed failure reason.
                        // Handle sign-in failure
                        signInViewModel.signInFailure()
                    }
                }
            }


        manager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        this.wifiDirectViewModel =
            ViewModelProvider(this,
                WifiDirectViewModelFactory(manager, channel))[WifiDirectViewModel::class.java]

        receiver = WifiDirectBroadcastReceiver(this.wifiDirectViewModel)

        // Register the BroadcastReceiver with the intent values to be matched
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        registerReceiver(receiver, intentFilter)

        setContent {
            VaultNFCTheme {
                AppNavigation(baseContext)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        })
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }


    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    fun signInWithGoogle() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val signInIntent = signInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}


