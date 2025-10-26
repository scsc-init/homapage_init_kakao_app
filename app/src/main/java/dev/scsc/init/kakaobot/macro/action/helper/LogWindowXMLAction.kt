package dev.scsc.init.kakaobot.macro.action.helper

import android.util.Log
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.util.AccessibilityUtil

@Suppress("unused")
class LogWindowXMLAction : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        Log.d("nodeWindowXML", AccessibilityUtil.dumpNodeToXml(executor.rootInActiveWindow))
    }
}
