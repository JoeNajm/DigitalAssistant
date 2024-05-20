package com.example.digitalassistant

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun createNotification(context: Context, notificationText: String): Notification {
    val notificationBuilder = NotificationCompat.Builder(context, "channel_id")
        .setSmallIcon(R.drawable.notif)
        .setContentTitle("Digital Amigo")
        .setContentText(notificationText)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    return notificationBuilder.build()
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Scheduled Notifications"
        val descriptionText = "Channel for scheduled notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("channel_id", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val POST_NOTIFICATIONS_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                ""
            }

            val notificationText = intent?.getStringExtra("NOTIFICATION_TEXT") ?: "Default Notification Text"

            val notification = createNotification(it, notificationText)
            val notificationManager = NotificationManagerCompat.from(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(it, POST_NOTIFICATIONS_PERMISSION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationManager.notify(1, notification)
        }
    }
}

fun scheduleNotification(context: Context, timeInMillis: Long, notificationText: String) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("NOTIFICATION_TEXT", notificationText)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}