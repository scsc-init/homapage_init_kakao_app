package dev.scsc.init.kakaobot.ui.screen.regularchat

import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.scsc.init.kakaobot.MyAccessibilityService
import dev.scsc.init.kakaobot.macro.MacroActionType
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.serialization.Serializable

@Serializable
object CreateRegularChatObj

@Composable
fun CreateRegularChatScreen() {
    val context = LocalContext.current
    var roomName by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }
    val friends = remember { mutableStateListOf<String>() }

    fun onClick(roomName: String, friends: ArrayList<String>) {
        if (!AccessibilityUtil.isAccessibilityServiceEnabled(
                context,
                MyAccessibilityService::class.java,
            )
        ) {
            Toast.makeText(
                context,
                "Please enable the accessibility service first.",
                Toast.LENGTH_LONG,
            ).show()
            return
        }
        if (roomName.isBlank() || friends.isEmpty()) {
            Toast.makeText(context, "Please enter both roomName and friends.", Toast.LENGTH_LONG)
                .show()
            return
        }
        val intent = Intent(context, MyAccessibilityService::class.java)
        intent.action = MyAccessibilityService.ACTION_RUN_MACRO
        intent.putExtra("macroActionType", MacroActionType.CREATE_REGULAR_CHAT as Parcelable)
        intent.putExtra("roomName", roomName)
        intent.putExtra("friends", friends)
        context.startService(intent)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("CreateRegularChatScreen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Enter room name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = friendName,
            onValueChange = { friendName = it },
            label = { Text("Enter friend name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (friendName.isNotBlank()) {
                    friends.add(friendName)
                    friendName = ""
                } else {
                    Toast.makeText(context, "Please enter a friend name", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Friend")
        }
        Button(
            onClick = { friends.clear() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Clear Friend")
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(friends) { Text(it) }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onClick(roomName, ArrayList(friends)) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run Macro")
        }
    }
}
