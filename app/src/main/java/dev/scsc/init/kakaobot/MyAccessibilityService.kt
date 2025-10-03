package dev.scsc.init.kakaobot

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        Log.d(
            "a11y event",
            event.eventType.toString() + event.text.toString() + event.source?.paneTitle
        )
        if (targetText == null) return

        val rootNode = rootInActiveWindow
        if (rootNode == null) return

        // Find and click the text
        findAndClickText(rootNode, targetText)
    }

    private fun findAndClickText(
        node: AccessibilityNodeInfo?,
        text: String?,
    ) {
        if (node == null) return

        val nodeText = node.getText()
        if (nodeText != null && nodeText.toString() == text) {
            var cur = node
            while (cur != null) {
                Log.d("node text", cur.text?.toString() ?: "null")
                if (cur.isClickable) {
                    cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    targetText = null // reset after clicking
                    return
                }
                cur = cur.parent
            }
        }

        for (i in 0..<node.childCount) {
            findAndClickText(node.getChild(i), text)
        }
    }

    override fun onInterrupt() {}

    companion object {
        private var targetText: String? = null

        fun setTargetText(text: String?) {
            targetText = text
        }
    }
}
