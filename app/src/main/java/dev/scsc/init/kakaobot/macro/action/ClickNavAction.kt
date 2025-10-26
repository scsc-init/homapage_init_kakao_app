package dev.scsc.init.kakaobot.macro.action

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClickNavAction(val title: MainTabTitle) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        LogWindowXMLAction().execute(executor)
        withContext(Dispatchers.Main.immediate) {
            val clickNode = executor.findBottomTabNavNode(title) ?: return@withContext
            Log.d("test", "test")
            clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
