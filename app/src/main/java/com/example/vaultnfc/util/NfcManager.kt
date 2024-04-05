package com.example.vaultnfc.util

import android.app.Activity
import android.nfc.NfcAdapter

class NfcManager(private val activity: Activity) {

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)

    fun isNfcSupported(): Boolean = nfcAdapter != null

    fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled ?: false

    // Add more NFC handling methods as needed
}
