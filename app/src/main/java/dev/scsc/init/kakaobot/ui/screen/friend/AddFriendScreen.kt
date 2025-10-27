package dev.scsc.init.kakaobot.ui.screen.friend

import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.scsc.init.kakaobot.MyAccessibilityService
import dev.scsc.init.kakaobot.macro.MacroActionType
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.serialization.Serializable

@Serializable
object AddFriendObj

@Composable
fun AddFriendScreen() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    fun onClick(name: String, phone: String) {
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
        if (name.isBlank() || phone.isBlank()) {
            Toast.makeText(context, "Please enter both name and phone.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(context, MyAccessibilityService::class.java)
        intent.action = MyAccessibilityService.ACTION_RUN_MACRO
        intent.putExtra("macroActionType", MacroActionType.ADD_FRIEND as Parcelable)
        intent.putExtra("name", name)
        intent.putExtra("phone", phone)
        context.startService(intent)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("AddFriendScreen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Enter phone number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onClick(name, phone) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run Macro")
        }
    }
}
