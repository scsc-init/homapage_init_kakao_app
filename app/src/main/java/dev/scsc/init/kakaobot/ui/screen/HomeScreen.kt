package dev.scsc.init.kakaobot.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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

    Column(modifier = Modifier.fillMaxSize()) {
        Text("HomeScreen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        PushGrantButton(Modifier.fillMaxWidth()) { requestPushPermission() }
    }
}


@Composable
private fun PushGrantButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Grant Push Permission")
    }
}
