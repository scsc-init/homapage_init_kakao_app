package dev.scsc.init.kakaobot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.scsc.init.kakaobot.util.AccessibilityUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request accessibility permission
        if (!AccessibilityUtil.isAccessibilityServiceEnabled(
                this,
                MyAccessibilityService::class.java
            )
        ) {
            // Open accessibility settings so user can enable service
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        setContent {
            MacroAppUI { inputText ->
                if (inputText.isNotBlank()) {
                    // Pass text to AccessibilityService
                    MyAccessibilityService.setTargetText(inputText)
                    // Launch KakaoTalk
                    val launchIntent = packageManager.getLaunchIntentForPackage("com.kakao.talk")
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(launchIntent)
                    }
                }
            }
        }
    }
}

@Composable
fun MacroAppUI(onRunClicked: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text to find") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRunClicked(text) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run Macro")
        }
    }
}
