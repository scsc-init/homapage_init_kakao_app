package dev.scsc.init.kakaobot.macro

interface MacroAction {
    fun execute(executor: MacroExecutor)
}
