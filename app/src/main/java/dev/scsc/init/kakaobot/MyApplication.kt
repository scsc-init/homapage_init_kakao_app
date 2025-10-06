package dev.scsc.init.kakaobot

//noinspection SuspiciousImport
import android.R
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat


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

    fun createNotification(title: String, content: String, channel: String = "my_channel_id") {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_dialog_info) // Your app's notification icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Set the action for tapping
            .setAutoCancel(true) // Dismiss the notification when tapped
        val notificationId = System.currentTimeMillis().toInt() // A unique ID for your notification
        notificationManager?.notify(notificationId, builder.build())
    }
}
