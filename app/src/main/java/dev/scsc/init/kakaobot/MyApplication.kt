package dev.scsc.init.kakaobot

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name: CharSequence = "My Notification Channel" // User-visible name
        val description = "Channel for my app's notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("my_channel_id", name, importance)
        channel.description = description
        // Register the channel with the system
        val notificationManager = getSystemService(NotificationManager::class.java)
        // 3. Log the creation attempt
        if (notificationManager == null) {
            Log.e("NotifChannel", "NotificationManager is null! Cannot create channel.")
        } else {
            notificationManager.createNotificationChannel(channel)
            Log.d("NotifChannel", "Channel 'my_channel_id' created successfully.")
        }
    }
}
