package dev.scsc.init.kakaobot.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityUtil {
    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<*>,
    ): Boolean {
        val expectedComponentName = ComponentName(context, service).flattenToString()
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

    fun dumpNodeToXml(node: AccessibilityNodeInfo?, indent: String = ""): String {
        if (node == null) return ""
        val sb = StringBuilder()

        val desc = node.contentDescription ?: ""
        val text = node.text ?: ""
        val id = node.viewIdResourceName ?: ""
        val cls = node.className ?: ""

        sb.append("$indent<node class=\"$cls\" text=\"$text\" desc=\"$desc\" id=\"$id\" clickable=\"${node.isClickable}\">\n")

        for (i in 0 until node.childCount) {
            sb.append(dumpNodeToXml(node.getChild(i), "$indent  "))
        }

        sb.append("$indent</node>\n")
        return sb.toString()
    }

}
