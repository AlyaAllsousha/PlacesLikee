package com.example.placeslikee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.placeslikee.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("my log", "Пуш пришел в открытое приложение! Title: ${message.data["markerId"]}")

        val data = message.data

        val title = data["title"] ?: "Новое уведомление"
        val body = data["body"] ?: ""
        val markerId = data["markerId"] ?: ""

        showNotification(title, body, markerId)
    }


    private fun showNotification(title: String, body: String, markerId: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "new_marker_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Новые места от подписок",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о новых локациях"
            }
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("placeslikee://marker/$markerId"),
            this,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        }
        val pendingIntent = PendingIntent.getActivity(
            this, markerId.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.marker_pointer, "Посмотреть на карте", pendingIntent)
        val notificationId = markerId.hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}