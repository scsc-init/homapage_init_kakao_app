package dev.scsc.init.kakaobot.macro.action

import android.util.Log
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.coroutines.delay

class LogWindowXMLAction : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        Log.d("rootWindowXML", AccessibilityUtil.dumpNodeToXml(executor.rootInActiveWindow))
        delay(100)
    }
}
