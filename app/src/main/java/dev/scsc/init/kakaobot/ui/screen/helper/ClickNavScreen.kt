package dev.scsc.init.kakaobot.ui.screen.helper

import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import dev.scsc.init.kakaobot.MyAccessibilityService
import dev.scsc.init.kakaobot.macro.MacroActionType
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.serialization.Serializable

@Serializable
object ClickNavObj

@Composable
fun ClickNavScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    fun onClick(text: String) {
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
        if (text.isBlank()) {
            Toast.makeText(context, "Please enter text to find.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(context, MyAccessibilityService::class.java)
        intent.action = MyAccessibilityService.ACTION_RUN_MACRO
        intent.putExtra("macroActionType", MacroActionType.CLICK_NAV as Parcelable)
        intent.putExtra("targetText", text)
        context.startService(intent)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("ClickNavScreen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text to find") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onClick(text) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run Macro")
        }
    }
}
