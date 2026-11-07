package com.example.droidlab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.droidlab.ui.screens.*

/**
 * Screen destinations matching Droid Lab exported pages.
 */
sealed class Screen(val route: String, val title: String, val icon: String) {
    data object Home : Screen("home", "Home", "home")
    data object Music : Screen("music", "Music", "music")
    data object Explore : Screen("explore", "Search", "search")
    data object Keep : Screen("keep", "Keeps", "keep")
    data object Settings : Screen("settings", "Settings", "settings")
    data object Equalizer : Screen("equalizer", "Equalizer", "equalizer")
}

val BottomNavScreens = listOf(
    Screen.Home,
    Screen.Music,
    Screen.Explore,
    Screen.Keep
)

@Composable
fun AppNavHost(
    navController: NavHostController,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    onPlaySong: (title: String, artist: String, openFullPlayer: Boolean) -> Unit = { t, a, _ -> onSongSelected(t, a) },
    currentSongTitle: String = "",
    isPlaying: Boolean = false,
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> },
    activeDeviceId: String = "phone_speaker",
    onDeviceSelected: (DeviceHardwarePreset) -> Unit = {},
    selectedFontPreference: String = "Manrope",
    onFontPreferenceChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                likedSongs = likedSongs,
                onToggleLike = onToggleLike,
                currentSongTitle = currentSongTitle,
                isPlaying = isPlaying
            )
        }
        composable(Screen.Music.route) {
            MusicRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                currentSongTitle = currentSongTitle,
                isPlaying = isPlaying,
                likedSongs = likedSongs,
                onToggleLike = onToggleLike
            )
        }
        composable(Screen.Explore.route) {
            ExploreRoute(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) }
            )
        }
        composable(Screen.Keep.route) {
            KeepsRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsRoute(
                onNavigateBack = { navController.popBackStack() },
                selectedFontPreference = selectedFontPreference,
                onFontPreferenceChanged = onFontPreferenceChanged
            )
        }
        composable(Screen.Equalizer.route) {
            EqualizerRoute(
                activeDeviceId = activeDeviceId,
                onDeviceSelected = onDeviceSelected,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
