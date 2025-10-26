package dev.scsc.init.kakaobot.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object AddFriendObj

@Composable
fun AddFriendScreen() {
    Box {
        Text("안녕")
    }
}
