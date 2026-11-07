package com.example.droidlab.navigation

import com.example.droidlab.data.entity.SongEntity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.droidlab.ui.screens.*
import com.example.droidlab.ui.viewmodel.MusicViewModel

/**
 * Screen destinations matching Droid Lab exported pages.
 * Bottom nav displays: Home, Music, Playlist, Keeps.
 * Dedicated search tab removed; Search is an overlay/destination opened from any top bar search button.
 */
sealed class Screen(val route: String, val title: String, val icon: String) {
    data object Home : Screen("home", "Home", "home")
    data object Music : Screen("music", "Music", "music")
    data object Playlist : Screen("playlist", "Playlist", "playlist")
    data object Keep : Screen("keep", "Keeps", "keep")
    data object Search : Screen("search", "Search", "search")
    data object Settings : Screen("settings", "Settings", "settings")
    data object Equalizer : Screen("equalizer", "Equalizer", "equalizer")
}

val BottomNavScreens = listOf(
    Screen.Home,
    Screen.Music,
    Screen.Playlist,
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
    selectedMiniPlayerStyle: String = "Classic Pill",
    onMiniPlayerStyleChanged: (String) -> Unit = {},
    onAlbumSelected: (AlbumItem) -> Unit = {},
    onArtistSelected: (ArtistItem) -> Unit = {},
    musicViewModel: MusicViewModel? = null,
    onScanRequested: () -> Unit = {},
    onPlaySongEntity: ((SongEntity, List<SongEntity>) -> Unit)? = null,
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
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenEqualizer = { navController.navigate(Screen.Equalizer.route) },
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                likedSongs = likedSongs,
                onToggleLike = onToggleLike,
                currentSongTitle = currentSongTitle,
                isPlaying = isPlaying,
                onAlbumSelected = onAlbumSelected,
                onNavigateToMusicTab = { navController.navigate(Screen.Music.route) },
                musicViewModel = musicViewModel,
                onPlaySongEntity = onPlaySongEntity
            )
        }
        composable(Screen.Music.route) {
            MusicRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenEqualizer = { navController.navigate(Screen.Equalizer.route) },
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                currentSongTitle = currentSongTitle,
                isPlaying = isPlaying,
                likedSongs = likedSongs,
                onToggleLike = onToggleLike,
                selectedMiniPlayerStyle = selectedMiniPlayerStyle,
                onMiniPlayerStyleChanged = onMiniPlayerStyleChanged,
                onAlbumSelected = onAlbumSelected,
                onArtistSelected = onArtistSelected,
                musicViewModel = musicViewModel,
                onScanRequested = onScanRequested,
                onPlaySongEntity = onPlaySongEntity
            )
        }
        composable(Screen.Playlist.route) {
            PlaylistRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenEqualizer = { navController.navigate(Screen.Equalizer.route) },
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                currentSongTitle = currentSongTitle,
                isPlaying = isPlaying,
                likedSongs = likedSongs,
                onToggleLike = onToggleLike,
                musicViewModel = musicViewModel,
                onPlaySongEntity = onPlaySongEntity
            )
        }
        composable(Screen.Keep.route) {
            KeepsRoute(
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenEqualizer = { navController.navigate(Screen.Equalizer.route) },
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) }
            )
        }
        composable(Screen.Search.route) {
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
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                onAlbumSelected = onAlbumSelected,
                onArtistSelected = onArtistSelected
            )
        }
        // Backward-compatibility alias for explore
        composable("explore") {
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
                onSongSelected = { title, artist -> onPlaySong(title, artist, false) },
                onAlbumSelected = onAlbumSelected,
                onArtistSelected = onArtistSelected
            )
        }
        composable(Screen.Settings.route) {
            SettingsRoute(
                onNavigateBack = { navController.popBackStack() },
                selectedFontPreference = selectedFontPreference,
                onFontPreferenceChanged = onFontPreferenceChanged,
                selectedMiniPlayerStyle = selectedMiniPlayerStyle,
                onMiniPlayerStyleChanged = onMiniPlayerStyleChanged
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
