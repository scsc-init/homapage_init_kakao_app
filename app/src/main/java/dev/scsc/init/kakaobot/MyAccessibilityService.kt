package dev.scsc.init.kakaobot

//noinspection SuspiciousImport
import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import dev.scsc.init.kakaobot.macro.MacroExecutor


class MyAccessibilityService : AccessibilityService() {
    private val macroExecutor = MacroExecutor(this)
    private val myApplication get() = application as MyApplication?

    companion object {
        const val ACTION_RUN_MACRO = "dev.scsc.kakaobot.ACTION_RUN_MACRO"
    }

    override fun onCreate() {
        super.onCreate()
        myApplication?.createNotification(
            "Service Created",
            "Service Created"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        macroExecutor.cancelAll()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_RUN_MACRO) {
            val targetText = intent.getStringExtra("targetText") ?: return START_NOT_STICKY
            val bundle = Bundle()
            bundle.putString("targetText", targetText)
            macroExecutor.executeMacro(MacroExecutor.Action.CLICK_TEXT, bundle)
        }
        return START_NOT_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
