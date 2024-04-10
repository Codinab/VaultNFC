package com.example.vaultnfc.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Opens the application's specific settings screen in the Android system settings.
 *
 * @param context The Context from which the settings screen is to be opened. This is
 * necessary to access the package name of the application and to start the activity
 * that will display the settings screen.
 */
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
