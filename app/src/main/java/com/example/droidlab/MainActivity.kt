package com.example.droidlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.droidlab.navigation.AppNavHost
import com.example.droidlab.navigation.BottomNavScreens
import com.example.droidlab.navigation.Screen
import com.example.droidlab.ui.components.*
import com.example.droidlab.ui.screens.AvailableDevicePresets
import com.example.droidlab.ui.screens.DeviceHardwarePreset
import com.example.droidlab.ui.screens.EqualizerRoute
import com.example.droidlab.ui.theme.DroidLabTheme
import com.example.droidlab.ui.theme.ManropeFontFamily
import com.example.droidlab.ui.theme.SpaceGroteskFontFamily
import com.example.droidlab.ui.theme.SystemFontFamily
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedFontPreference by remember { mutableStateOf("Manrope") }
            val activeFontFamily = when (selectedFontPreference) {
                "Space Grotesk" -> SpaceGroteskFontFamily
                "System Font" -> SystemFontFamily
                else -> ManropeFontFamily
            }
            DroidLabTheme(fontFamily = activeFontFamily) {
                AppRoot(
                    selectedFontPreference = selectedFontPreference,
                    onFontPreferenceChanged = { selectedFontPreference = it }
                )
            }
        }
    }
}

@Composable
fun AppRoot(
    selectedFontPreference: String = "Manrope",
    onFontPreferenceChanged: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var isNowPlayingOpen by remember { mutableStateOf(false) }
    var isEqualizerOpen by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf<Int?>(null) }

    // Connected Audio Device / Hardware Profile (reflected in player and throughout app)
    var activeDevicePreset by remember { mutableStateOf<DeviceHardwarePreset>(AvailableDevicePresets.first()) }

    var currentSongTitle by remember { mutableStateOf("Midnight Drive") }
    var currentSongArtist by remember { mutableStateOf("Nova Lane") }
    var isPlaying by remember { mutableStateOf(true) }

    // Shared list of liked songs
    val likedSongs = remember {
        mutableStateListOf(
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Blue Bottle Coffee", "Chillhop Beats")
        )
    }

    val isCurrentSongLiked = likedSongs.any { it.first == currentSongTitle && it.second == currentSongArtist }

    val toggleLike: (String, String) -> Unit = { title, artist ->
        val exists = likedSongs.any { it.first == title && it.second == artist }
        if (exists) {
            likedSongs.removeAll { it.first == title && it.second == artist }
        } else {
            likedSongs.add(Pair(title, artist))
        }
    }

    val bottomNavIndex = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.Music.route -> 1
        Screen.Explore.route -> 2
        Screen.Keep.route -> 3
        else -> -1
    }

    // Mini Player ONLY shown at Home and Music pages, nothing else
    val showMiniPlayer = currentRoute == Screen.Home.route || currentRoute == Screen.Music.route

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
                    coroutineScope.launch { drawerState.close() }
                },
                onClose = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (bottomNavIndex >= 0) {
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
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Content scrolls freely all the way down to bottom nav
                AppNavHost(
                    navController = navController,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenSearch = navigateToSearch,
                    onSongSelected = { title, artist ->
                        currentSongTitle = title
                        currentSongArtist = artist
                        isPlaying = true
                    },
                    likedSongs = likedSongs,
                    onToggleLike = toggleLike,
                    activeDeviceId = activeDevicePreset.id,
                    onDeviceSelected = { activeDevicePreset = it },
                    selectedFontPreference = selectedFontPreference,
                    onFontPreferenceChanged = onFontPreferenceChanged,
                    modifier = Modifier.fillMaxSize()
                )

                // Floating Mini Player: ONLY at Home and Music pages, with heart like button
                if (showMiniPlayer) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        DroidLabMiniPlayer(
                            title = currentSongTitle,
                            artist = currentSongArtist,
                            progressPercent = 42,
                            isPlaying = isPlaying,
                            isLiked = isCurrentSongLiked,
                            onToggleLike = { toggleLike(currentSongTitle, currentSongArtist) },
                            onPlayPause = { isPlaying = !isPlaying },
                            onNext = {
                                currentSongTitle = "Neon Horizon"
                                currentSongArtist = "Retro Wave"
                                isPlaying = true
                            },
                            onClick = { isNowPlayingOpen = true }
                        )
                    }
                }
            }

            // True Full-Screen Now Playing Experience (0dp corner radius, edge-to-edge)
            AnimatedVisibility(
                visible = isNowPlayingOpen,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(200)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(200))
            ) {
                DroidLabNowPlayingScreen(
                    title = currentSongTitle,
                    artist = currentSongArtist,
                    album = "Neon Hours",
                    progress = 0.42f,
                    currentTime = "1:48",
                    totalTime = "4:12",
                    isPlaying = isPlaying,
                    isLiked = isCurrentSongLiked,
                    activeDeviceName = activeDevicePreset.name,
                    activeDeviceIcon = activeDevicePreset.iconName,
                    onOpenEqualizer = { isEqualizerOpen = true },
                    onToggleLike = { toggleLike(currentSongTitle, currentSongArtist) },
                    onPlayPause = { isPlaying = !isPlaying },
                    onPrevious = {
                        currentSongTitle = "Blue Bottle Coffee"
                        currentSongArtist = "Chillhop Beats"
                    },
                    onNext = {
                        currentSongTitle = "Neon Horizon"
                        currentSongArtist = "Retro Wave"
                    },
                    onClose = { isNowPlayingOpen = false },
                    sleepTimerMinutes = sleepTimerMinutes,
                    onSetSleepTimer = { mins -> sleepTimerMinutes = mins }
                )
            }

            // Full-screen Equalizer View (accessible from Player device pill & dropdown menu)
            AnimatedVisibility(
                visible = isEqualizerOpen,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(200)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(200))
            ) {
                EqualizerRoute(
                    activeDeviceId = activeDevicePreset.id,
                    onDeviceSelected = { preset ->
                        activeDevicePreset = preset
                    },
                    onNavigateBack = { isEqualizerOpen = false }
                )
            }
        }
    }
}
