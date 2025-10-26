package dev.scsc.init.kakaobot.macro.action.friend

import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.action.LogWindowXMLAction

class AddFriendAction(val name: String, val phone: String) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        LogWindowXMLAction().execute(executor)

    }
}
