package dev.scsc.init.kakaobot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.core.content.ContextCompat
import dev.scsc.init.kakaobot.macro.MacroActionType
import dev.scsc.init.kakaobot.util.AccessibilityUtil


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
            Box(
                Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                PushGrantButton(Modifier.fillMaxWidth()) { requestPushPermission() }
                MacroAppUI { inputText ->
                    if (!AccessibilityUtil.isAccessibilityServiceEnabled(
                            this@MainActivity,
                            MyAccessibilityService::class.java,
                        )
                    ) {
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Please enable the accessibility service first.",
                                Toast.LENGTH_LONG,
                            )
                            .show()
                        return@MacroAppUI
                    }
                    if (inputText.isNotBlank()) {
                        // Pass text to AccessibilityService
                        val intent = Intent(this@MainActivity, MyAccessibilityService::class.java)
                        intent.action = MyAccessibilityService.ACTION_RUN_MACRO
                        intent.putExtra("macroActionType", MacroActionType.CLICK_TEXT as Parcelable)
                        intent.putExtra("targetText", inputText)
                        this@MainActivity.startService(intent)
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, you can now send notifications
                Toast
                    .makeText(this, "Push permission granted.", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast
                    .makeText(this, "Please grant push permission", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun requestPushPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Use the launcher declared as a class property
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Toast
                .makeText(this, "Push permission has already granted", Toast.LENGTH_LONG)
                .show()
        }
    }
}

@Composable
fun MacroAppUI(onRunClicked: (String) -> Unit) {
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
fun PushGrantButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Grant Push Permission")
    }
}
