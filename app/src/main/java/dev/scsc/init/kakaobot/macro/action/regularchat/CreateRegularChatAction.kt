package dev.scsc.init.kakaobot.macro.action.regularchat

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle
import dev.scsc.init.kakaobot.macro.action.helper.ClickNavAction

class CreateRegularChatAction(val roomName: String, val friends: List<String>) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        // 채팅 탭 이동
        ClickNavAction(MainTabTitle.CHAT).execute(executor)
        executor.clickByText("새로운 채팅", MacroExecutor.SearchByOption.DESC)
        executor.clickByText("일반채팅", MacroExecutor.SearchByOption.DESC)
        friends.forEach {
            // 친구 이름 입력
            executor.retryUntilTrue {
                val edittext = executor.findNodeByText(
                    executor.rootInActiveWindow,
                    "이름(초성), 전화번호 검색",
                )
                val bundle = Bundle()
                bundle.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    it
                )
                edittext.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
            }
            // 친구 선택
            executor.clickByText(it, MacroExecutor.SearchByOption.DESC)
            executor.clickByText("입력한 내용 삭제", MacroExecutor.SearchByOption.DESC)
        }
        executor.clickByText("다음", MacroExecutor.SearchByOption.TEXT)
        // 친구 목록이 동일한 채팅방이 이미 존재하면 새로운 채팅방 만들기 버튼 클릭
        executor.clickByText("새로운 채팅방 만들기", MacroExecutor.SearchByOption.TEXT)
        // 채팅방 이름 입력
        executor.retryUntilTrue {
            val bundle = Bundle()
            bundle.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                roomName
            )
            val node = executor.focusInputNode
            node.text == friends.sorted().joinToString()
                    && node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
        }
        executor.clickByText("확인", MacroExecutor.SearchByOption.TEXT)
        // 첫 메시지 입력
        executor.retryUntilTrue {
            val bundle = Bundle()
            bundle.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                "SCSC 봇이 $roomName 일반채팅을 만들었습니다."
            )
            val node = executor.focusInputNode
            node.className == "android.widget.MultiAutoCompleteTextView"
                    && node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
        }
        executor.clickByText("전송", MacroExecutor.SearchByOption.DESC)
    }
}
