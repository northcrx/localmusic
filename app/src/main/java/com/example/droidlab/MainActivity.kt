package com.example.droidlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.droidlab.navigation.AppNavHost
import com.example.droidlab.navigation.BottomNavScreens
import com.example.droidlab.navigation.Screen
import com.example.droidlab.ui.components.*
import com.example.droidlab.ui.theme.DroidLabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidLabTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var isNowPlayingOpen by remember { mutableStateOf(false) }

    var currentSongTitle by remember { mutableStateOf("Midnight Drive") }
    var currentSongArtist by remember { mutableStateOf("Nova Lane") }
    var isPlaying by remember { mutableStateOf(true) }

    val bottomNavIndex = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.Music.route -> 1
        Screen.Explore.route -> 2
        Screen.Keep.route -> 3
        else -> -1
    }

    val navigateToSearch: () -> Unit = {
        if (currentRoute != Screen.Explore.route) {
            navController.navigate(Screen.Explore.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DroidLabDrawerSheet(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onClose = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(0.dp),
                bottomBar = {
                    if (bottomNavIndex != -1) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            DroidLabMiniPlayer(
                                title = currentSongTitle,
                                artist = currentSongArtist,
                                progressPercent = 64,
                                isPlaying = isPlaying,
                                onPlayPause = { isPlaying = !isPlaying },
                                onNext = {
                                    currentSongTitle = "Neon Horizon"
                                    currentSongArtist = "Retro Wave"
                                    isPlaying = true
                                },
                                onClick = { isNowPlayingOpen = true }
                            )
                            DroidLabBottomNav(
                                selectedIndex = bottomNavIndex,
                                onTabSelected = { index ->
                                    val targetScreen = BottomNavScreens[index]
                                    if (currentRoute != targetScreen.route) {
                                        navController.navigate(targetScreen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                AppNavHost(
                    navController = navController,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenSearch = navigateToSearch,
                    onSongSelected = { title, artist ->
                        currentSongTitle = title
                        currentSongArtist = artist
                        isPlaying = true
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Full Now Playing Sheet (Modal Vinyl Player)
            DroidLabNowPlayingSheet(
                visible = isNowPlayingOpen,
                title = currentSongTitle,
                artist = currentSongArtist,
                isPlaying = isPlaying,
                onPlayPause = { isPlaying = !isPlaying },
                onPrevious = {
                    currentSongTitle = "Blue Bottle Coffee"
                    currentSongArtist = "Chillhop Beats"
                },
                onNext = {
                    currentSongTitle = "Neon Horizon"
                    currentSongArtist = "Retro Wave"
                },
                onDismiss = { isNowPlayingOpen = false }
            )
        }
    }
}
