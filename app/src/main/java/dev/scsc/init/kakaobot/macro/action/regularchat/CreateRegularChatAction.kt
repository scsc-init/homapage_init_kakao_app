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
        // 새로운 채팅 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "새로운 채팅",
                searchByOption = MacroExecutor.SearchByOption.DESC
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        // 일반채팅 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "일반채팅",
                searchByOption = MacroExecutor.SearchByOption.DESC
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
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
            executor.retryUntilTrue {
                val btn = executor.findNearestClickableParent(
                    executor.findNodeByText(
                        executor.rootInActiveWindow,
                        it,
                        searchByOption = MacroExecutor.SearchByOption.DESC
                    )
                ) ?: throw IllegalStateException("cannot click friend")
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            // 입력한 내용 삭제 버튼 클릭
            executor.retryUntilTrue {
                val btn = executor.findNodeByText(
                    executor.rootInActiveWindow,
                    "입력한 내용 삭제",
                    searchByOption = MacroExecutor.SearchByOption.DESC
                )
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
        // 다음 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "다음",
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
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
        // 확인 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "확인",
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
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
        // 전송 버튼 클릭
        executor.retryUntilTrue {
            val btn = executor.findNodeByText(
                executor.rootInActiveWindow,
                "전송",
                searchByOption = MacroExecutor.SearchByOption.DESC
            )
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
