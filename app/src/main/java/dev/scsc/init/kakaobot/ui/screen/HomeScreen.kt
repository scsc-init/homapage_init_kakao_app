package dev.scsc.init.kakaobot.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.scsc.init.kakaobot.MyAccessibilityService
import dev.scsc.init.kakaobot.macro.MacroActionType
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.serialization.Serializable

@Serializable
object HomeObj

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
            val msg = if (isGranted) "Push permission granted." else "Please grant push permission"
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

    val requestPushPermission = {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Toast
                .makeText(context, "Push permission has already granted", Toast.LENGTH_LONG)
                .show()
        }
    }

    Column {
        PushGrantButton(Modifier.fillMaxWidth()) { requestPushPermission() }
        MacroAppUI { inputText ->
            if (!AccessibilityUtil.isAccessibilityServiceEnabled(
                    context,
                    MyAccessibilityService::class.java,
                )
            ) {
                Toast
                    .makeText(
                        context,
                        "Please enable the accessibility service first.",
                        Toast.LENGTH_LONG,
                    )
                    .show()
                return@MacroAppUI
            }
            if (inputText.isNotBlank()) {
                // Pass text to AccessibilityService
                val intent = Intent(context, MyAccessibilityService::class.java)
                intent.action = MyAccessibilityService.ACTION_RUN_MACRO
                intent.putExtra("macroActionType", MacroActionType.CLICK_NAV as Parcelable)
                intent.putExtra("targetText", inputText)
                context.startService(intent)
            }
        }
    }
}

@Composable
private fun MacroAppUI(onRunClicked: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
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

@Composable
private fun PushGrantButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Grant Push Permission")
    }
}
