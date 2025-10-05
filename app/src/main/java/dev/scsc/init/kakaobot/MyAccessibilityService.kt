package dev.scsc.init.kakaobot

//noinspection SuspiciousImport
import android.R
import android.accessibilityservice.AccessibilityService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import dev.scsc.init.kakaobot.macro.MacroExecutor


class MyAccessibilityService : AccessibilityService() {
    private val macroExecutor = MacroExecutor(this)
    override fun onCreate() {
        super.onCreate()
        createNotification(
            "Service Created",
            "Service Created"
        )
    }

    private fun createNotification(title: String, content: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, "my_channel_id")
            .setSmallIcon(R.drawable.ic_dialog_info) // Your app's notification icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Set the action for tapping
            .setAutoCancel(true) // Dismiss the notification when tapped
        val notificationId = System.currentTimeMillis().toInt() // A unique ID for your notification
        notificationManager?.notify(notificationId, builder.build())
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val bundle = Bundle()
        bundle.putString("targetText", targetText)
        macroExecutor.executeMacro(MacroExecutor.Action.CLICK_TEXT, bundle)
        targetText = null
    }

    override fun onInterrupt() {}

    companion object {
        @Volatile
        private var targetText: String? = null

        fun setTargetText(text: String?) {
            targetText = text
        }
    }
}
