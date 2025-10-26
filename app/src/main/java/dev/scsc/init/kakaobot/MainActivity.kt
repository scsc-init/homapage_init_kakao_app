package dev.scsc.init.kakaobot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.scsc.init.kakaobot.ui.screen.AddFriendObj
import dev.scsc.init.kakaobot.ui.screen.AddFriendScreen
import dev.scsc.init.kakaobot.ui.screen.ClickNavObj
import dev.scsc.init.kakaobot.ui.screen.ClickNavScreen
import dev.scsc.init.kakaobot.ui.screen.HomeObj
import dev.scsc.init.kakaobot.ui.screen.HomeScreen
import dev.scsc.init.kakaobot.util.AccessibilityUtil
import kotlinx.coroutines.launch


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
            ) {
                App()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = false,
                        onClick = {
                            navController.navigate(HomeObj)
                            scope.launch { drawerState.close() }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Friend",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text("103001: AddFriend") },
                        selected = false,
                        onClick = {
                            navController.navigate(AddFriendObj)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("103002: CheckFriend") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Helper",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text("ClickNav") },
                        selected = false,
                        onClick = {
                            navController.navigate(ClickNavObj)
                            scope.launch { drawerState.close() }
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("카카오톡 봇") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = HomeObj,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                composable<HomeObj> { HomeScreen() }
                composable<AddFriendObj> { AddFriendScreen() }
                composable<ClickNavObj> { ClickNavScreen() }
            }
        }
    }
}
