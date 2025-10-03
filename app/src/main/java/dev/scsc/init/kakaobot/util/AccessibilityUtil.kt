package dev.scsc.init.kakaobot.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils

object AccessibilityUtil {
    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<*>,
    ): Boolean {
        val expectedComponentName = "${context.packageName}/${service.name}"
        val enabledServices =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            )
        if (enabledServices.isNullOrEmpty()) return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        while (splitter.hasNext()) {
            val componentName = splitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
