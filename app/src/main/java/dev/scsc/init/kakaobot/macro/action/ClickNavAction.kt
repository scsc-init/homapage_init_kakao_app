package dev.scsc.init.kakaobot.macro.action

import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle

class ClickNavAction(val title: MainTabTitle) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        executor.retryUntilTrue {
            val clickNode = executor.findBottomTabNavNode(title)
                ?: throw IllegalStateException("cannot find bottomTabNavNode at ClickNavAction")
            clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
