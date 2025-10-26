package dev.scsc.init.kakaobot.macro.action.friend

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.macro.MacroAction
import dev.scsc.init.kakaobot.macro.MacroExecutor
import dev.scsc.init.kakaobot.macro.MainTabTitle
import dev.scsc.init.kakaobot.macro.action.ClickNavAction

class AddFriendAction(val name: String, val phone: String) : MacroAction {
    override suspend fun execute(executor: MacroExecutor) {
        // 친구 탭 이동
        ClickNavAction(MainTabTitle.FRIEND).execute(executor)
        // 친구 추가 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "친구 추가",
                searchByOption = MacroExecutor.SearchByOption.DESC
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        // 연락처(로 추가) 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "연락처",
                searchByOption = MacroExecutor.SearchByOption.DESC
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        // 친구 이름 입력
        executor.retryUntilTrue {
            val edittext = executor.findNodeByText(
                executor.rootInActiveWindow,
                "친구 이름",
            )
            val bundle = Bundle()
            bundle.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                name
            )
            edittext.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
        }
        // 전화번호 입력
        executor.retryUntilTrue {
            val edittext = executor.findNodeByText(
                executor.rootInActiveWindow,
                "전화번호",
            )
            val bundle = Bundle()
            bundle.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                phone
            )
            edittext.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
        }
        // 확인 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "확인",
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
