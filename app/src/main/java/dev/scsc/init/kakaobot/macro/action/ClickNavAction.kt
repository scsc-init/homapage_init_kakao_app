package dev.scsc.init.kakaobot.macro.action

import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle

class ClickNavAction(val title: MainTabTitle) : MacroAction {
    override fun execute(executor: MacroExecutor) {
        Thread.sleep(2000)
        val clickNode = executor.findBottomTabNavNode(title) ?: return
        clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }
}
