package dev.scsc.init.kakaobot.macro

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.MyApplication
import dev.scsc.init.kakaobot.macro.action.ClickNavAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class MacroExecutor(private val service: AccessibilityService) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val myApplication get() = service.application as MyApplication?

    /**
     * Call this method when the AccessibilityService is being destroyed
     * to clean up all running coroutines.
     */
    fun cancelAll() {
        scope.cancel()
    }

    @Volatile
    var isBusy: Boolean = false
        private set

    fun executeMacro(macroActionType: MacroActionType, extras: Bundle?) {
        if (isBusy) {
            myApplication?.createNotification(
                "Error on executeMacro",
                "Executor is busy now"
            )
            return
        }
        scope.launch {
            isBusy = true
            try {
                // Launch KakaoTalk
                val ctx = service.applicationContext ?: return@launch
                val launchIntent = ctx.packageManager.getLaunchIntentForPackage("com.kakao.talk")
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    ctx.startActivity(launchIntent)
                    // Wait for KakaoTalk to be ready
                    var attempts = 0
                    val maxAttempts = 30 // 3 seconds
                    while (attempts < maxAttempts) {
                        kotlinx.coroutines.delay(100)
                        if (rootInActiveWindow?.packageName == "com.kakao.talk") {
                            break
                        }
                        attempts++
                    }
                    if (attempts >= maxAttempts) {
                        myApplication?.createNotification(
                            "Error on executeMacro",
                            "KakaoTalk did not launch in time"
                        )
                        return@launch
                    }
                } else {
                    myApplication?.createNotification(
                        "Error on executeMacro",
                        "KakaoTalk is not installed"
                    )
                    return@launch
                }
                // Execute macroAction
                when (macroActionType) {
                    MacroActionType.CLICK_TEXT -> {
                        val text = extras?.getString("targetText") ?: return@launch
                        val title = text.toMainTabTitleOrNull() ?: return@launch

                        ClickNavAction(title).execute(this@MacroExecutor)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                myApplication?.createNotification("Error on executeMacro", "$macroActionType")
            } finally {
                isBusy = false
            }
        }
    }

    val rootInActiveWindow: AccessibilityNodeInfo? get() = service.rootInActiveWindow
    val currentTabTitle: MainTabTitle?
        get() {
            val tabNode = rootInActiveWindow?.getChild(0)?.getChild(0)
            if (tabNode == null || tabNode.className != "android.widget.TextView") return null
            return tabNode.text.toString().toMainTabTitleOrNull()
        }

    enum class TextMatchOption {
        CONTAINS,
        EXACT
    }

    /**
     * Searches the subtree rooted at [rootNode] for all nodes whose 'text' attribute
     * matches the given [searchText] based on the [matchOption].
     *
     * This function strictly checks the 'text' property and explicitly ignores
     * the 'contentDescription' property (which corresponds to 'desc' in the XML structure).
     *
     * @param rootNode The starting node for the search (e.g., the root view).
     * @param searchText The text to search for (case-sensitive by default).
     * @param matchOption The criteria for matching the text (defaults to CONTAINS).
     * @return A list of AccessibilityNodeInfo objects whose 'text' matches the search string.
     */
    fun findNodeInfosByText(
        rootNode: AccessibilityNodeInfo?,
        searchText: String,
        matchOption: TextMatchOption = TextMatchOption.EXACT
    ): List<AccessibilityNodeInfo> {
        val foundNodes = mutableListOf<AccessibilityNodeInfo>()

        // Internal recursive function to perform a Depth-First Search (DFS)
        fun searchRecursively(node: AccessibilityNodeInfo?) {
            if (node == null || searchText.isEmpty()) return
            val nodeText = node.text?.toString()

            if (nodeText != null) {
                val isMatch = when (matchOption) {
                    TextMatchOption.CONTAINS -> nodeText.contains(searchText, ignoreCase = false)
                    TextMatchOption.EXACT -> nodeText == searchText
                }
                if (isMatch) {
                    foundNodes.add(node)
                }
            }

            val childCount = node.childCount
            for (i in 0 until childCount) {
                val child = node.getChild(i) ?: continue
                searchRecursively(child)
            }
        }

        searchRecursively(rootNode)
        return foundNodes
    }

    fun findBottomTabNavNode(title: MainTabTitle): AccessibilityNodeInfo? {
        val root = rootInActiveWindow ?: return null
        if (root.childCount != 2) return null
        val nav = root.getChild(1) ?: return null
        val textNodes = findNodeInfosByText(nav, title.str)
        if (textNodes.size != 1) return null
        val textNode = textNodes.getOrNull(0) ?: return null
        return findNearestClickableParent(textNode)
    }


    fun findNearestClickableParent(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var cur: AccessibilityNodeInfo? = node
        while (cur != null) {
            if (cur.isClickable) {
                return cur
            }
            cur = cur.parent
        }
        return null
    }
}


@Parcelize
enum class MacroActionType : Parcelable {
    CLICK_TEXT
}

enum class MainTabTitle(val str: String) {
    FRIEND("친구"),
    CHAT("채팅"),
    OPEN_CHAT("오픈채팅"),
    SHOP("쇼핑"),
    MORE("더보기")
}

fun String.toMainTabTitleOrNull(): MainTabTitle? = MainTabTitle.entries.find { it.str == this }
