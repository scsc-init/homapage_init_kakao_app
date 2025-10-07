package dev.scsc.init.kakaobot.macro

interface MacroAction {
    suspend fun execute(executor: MacroExecutor)
}
