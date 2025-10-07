package dev.scsc.init.kakaobot.macro.action

import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ClickNavAction(val title: MainTabTitle) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        delay(100)
        withContext(Dispatchers.Main.immediate) {
            val clickNode = executor.findBottomTabNavNode(title) ?: return@withContext
            clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
