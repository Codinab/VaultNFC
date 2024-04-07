package com.example.vaultnfc.ui.screens

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.ui.viewmodel.WifiDirectViewModel
import com.example.vaultnfc.util.WifiDirectViewModelFactory

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WifiDirectCommunication(navController: NavController) {
    val context = LocalContext.current

    val manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    val channel = manager.initialize(context, context.mainLooper, null)
    val viewModel: WifiDirectViewModel =
        viewModel(factory = WifiDirectViewModelFactory(manager, channel))
    val isWifiP2pEnabled by viewModel.isWifiP2pEnabled.observeAsState(false)
    val peers by viewModel.peers.observeAsState(emptyList())
    val connectionStatus by viewModel.connectionStatus.observeAsState("")
    val receivedMessage by viewModel.receivedMessage.observeAsState("")

    var textToSend by remember { mutableStateOf("") }

    viewModel.discoverPeers()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Wi-Fi Direct Status: ${if (isWifiP2pEnabled) "Enabled" else "Disabled"}")
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = textToSend,
            onValueChange = { textToSend = it },
            label = { Text("Message to send") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.sendText(textToSend)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Message")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text("Connection Status: $connectionStatus")
        Spacer(modifier = Modifier.height(8.dp))

        Text("Received Message: $receivedMessage")
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(peers) { peer ->
                PeerItem(peer) { selectedPeer ->
                    viewModel.connectToDevice(selectedPeer)
                }
            }
        }
    }
}

@Composable
fun PeerItem(peer: WifiP2pDevice, onConnect: (WifiP2pDevice) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onConnect(peer) }
        .padding(8.dp)) {
        Text(text = peer.deviceName ?: "Unknown Device", modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Tap to connect")
    }
    HorizontalDivider()
}







