package dev.scsc.init.kakaobot.macro

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import dev.scsc.init.kakaobot.MyApplication
import dev.scsc.init.kakaobot.macro.action.ClickNavAction
import dev.scsc.init.kakaobot.macro.action.friend.AddFriendAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
        isBusy = true
        scope.launch {
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
                        delay(100)
                        if (service.rootInActiveWindow?.packageName == "com.kakao.talk") {
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
                    MacroActionType.CLICK_NAV -> {
                        val text = extras?.getString("targetText") ?: return@launch
                        val title = text.toMainTabTitleOrNull() ?: return@launch
                        ClickNavAction(title).execute(this@MacroExecutor)
                    }

                    MacroActionType.ADD_FRIEND -> {
                        val name = extras?.getString("name") ?: return@launch
                        val phone = extras.getString("phone") ?: return@launch
                        AddFriendAction(name, phone).execute(this@MacroExecutor)
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

    val rootInActiveWindow: AccessibilityNodeInfo
        get() = service.rootInActiveWindow
            ?: throw IllegalStateException("cannot retrieve rootInActiveWindow")

    enum class TextMatchOption {
        CONTAINS,
        EXACT
    }

    enum class SearchByOption {
        TEXT,
        DESC
    }

    /**
     * Searches the subtree rooted at [rootNode] for all nodes whose [searchByOption] attribute
     * matches the given [searchText] based on the [matchOption].
     *
     * @param rootNode The starting node for the search (e.g., the root view).
     * @param searchText The text to search for (case-sensitive by default).
     * @param matchOption The criteria for matching the text (defaults to CONTAINS).
     * @param searchByOption The criteria for target attribute (defaults to TEXT).
     * @return A list of AccessibilityNodeInfo objects whose 'text' matches the search string.
     */
    fun findNodesByText(
        rootNode: AccessibilityNodeInfo?,
        searchText: String,
        matchOption: TextMatchOption = TextMatchOption.EXACT,
        searchByOption: SearchByOption = SearchByOption.TEXT
    ): List<AccessibilityNodeInfo> {
        val foundNodes = mutableListOf<AccessibilityNodeInfo>()

        // Internal recursive function to perform a Depth-First Search (DFS)
        fun searchRecursively(node: AccessibilityNodeInfo?) {
            if (node == null || searchText.isEmpty()) return
            val searchTarget = when (searchByOption) {
                SearchByOption.TEXT -> node.text?.toString()
                SearchByOption.DESC -> node.contentDescription?.toString()
            }

            if (searchTarget != null) {
                val isMatch = when (matchOption) {
                    TextMatchOption.CONTAINS -> searchTarget.contains(
                        searchText,
                        ignoreCase = false
                    )

                    TextMatchOption.EXACT -> searchTarget == searchText
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

    /**
     * Searches the subtree rooted at [rootNode] for all nodes whose [searchByOption] attribute
     * matches the given [searchText] based on the [matchOption].
     *
     * @param rootNode The starting node for the search (e.g., the root view).
     * @param searchText The text to search for (case-sensitive by default).
     * @param matchOption The criteria for matching the text (defaults to CONTAINS).
     * @param searchByOption The criteria for target attribute (defaults to TEXT).
     * @param checkUniqueness The criteria for asserting uniqueness (defaults to true).
     * @return A AccessibilityNodeInfo objects whose 'text' matches the search string.
     * @throws IllegalStateException if no nodes are found or multiple nodes are found when [checkUniqueness] is true
     */
    fun findNodeByText(
        rootNode: AccessibilityNodeInfo?,
        searchText: String,
        matchOption: TextMatchOption = TextMatchOption.EXACT,
        searchByOption: SearchByOption = SearchByOption.TEXT,
        checkUniqueness: Boolean = true
    ): AccessibilityNodeInfo {
        val nodes = findNodesByText(rootNode, searchText, matchOption, searchByOption)
        if (checkUniqueness) {
            if (nodes.size > 1) throw IllegalStateException("multiple nodes are found by findNodeByText; searchText=${searchText}")
        }
        return nodes.getOrNull(0)
            ?: throw IllegalStateException("no nodes are found by findNodeByText; searchText=${searchText}")
    }

    fun findBottomTabNavNode(title: MainTabTitle): AccessibilityNodeInfo? {
        val root = rootInActiveWindow
        if (root.childCount != 2) return null
        val nav = root.getChild(1) ?: return null
        val textNodes = findNodesByText(
            nav, title.str, TextMatchOption.CONTAINS,
            SearchByOption.DESC
        )
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

    val performDelay
        get() = myApplication?.performDelay
            ?: throw IllegalStateException("cannot retrieve myApplication on MacroExecutor")

    val performRetry
        get() = myApplication?.performRetry
            ?: throw IllegalStateException("cannot retrieve myApplication on MacroExecutor")

    suspend fun retryUntilTrue(f: () -> Boolean) {
        for (i in 0..performRetry) {
            delay(performDelay * i)
            val res = runCatching { f() }.getOrElse {
                it.printStackTrace()
                false
            }
            if (res) return
            Log.d("debug", "retry $i")
        }
    }
}


@Parcelize
enum class MacroActionType : Parcelable {
    CLICK_NAV,
    ADD_FRIEND
}

enum class MainTabTitle(val str: String) {
    FRIEND("친구 탭"),
    CHAT("채팅 탭"),
    OPEN_CHAT("오픈채팅"),
    SHOP("쇼핑 탭"),
    MORE("더보기 탭")
}

fun String.toMainTabTitleOrNull(): MainTabTitle? = MainTabTitle.entries.find { it.str == this }
