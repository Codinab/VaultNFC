package com.example.vaultnfc.util

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vaultnfc.MainActivity
import com.example.vaultnfc.R
import com.example.vaultnfc.data.preferences.NotificationPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val data = remoteMessage.data
        val channelId = remoteMessage.notification?.channelId ?: getString(R.string.password_update_channel_id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            handleNotification(title, body, channelId)
        } else {
            Toast.makeText(this, "$title: $body", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToFirestore(token)
    }

    private fun saveTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val deviceData = hashMapOf("token" to token)
        db.collection("users").document(userId).collection("devices").document(token).set(deviceData)
            .addOnSuccessListener {
                // Log or handle successful token saving
            }
            .addOnFailureListener { e ->
                // Log or handle failure in saving token
                e.printStackTrace()
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleNotification(title: String?, body: String?, channelId: String) {
        val notificationId = 1

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_menu)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permissions if not granted
                ActivityCompat.requestPermissions(MainActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                Toast.makeText(application, "$title: $body", Toast.LENGTH_SHORT).show()
                return
            }

            val notificationPreference = NotificationPreference(application)
            if (notificationPreference.isChannelEnabled(application, channelId))
                notify(notificationId, notificationBuilder.build())
        }
    }
}
