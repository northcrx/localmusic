package com.example.droidlab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.droidlab.navigation.AppNavHost
import com.example.droidlab.navigation.BottomNavScreens
import com.example.droidlab.navigation.Screen
import com.example.droidlab.ui.components.*
import com.example.droidlab.ui.screens.AlbumDetailScreen
import com.example.droidlab.ui.screens.AlbumItem
import com.example.droidlab.ui.screens.ArtistDetailScreen
import com.example.droidlab.ui.screens.ArtistItem
import com.example.droidlab.ui.screens.AvailableDevicePresets
import com.example.droidlab.ui.screens.DeviceHardwarePreset
import com.example.droidlab.ui.screens.EqualizerRoute
import com.example.droidlab.ui.theme.DroidLabTheme
import com.example.droidlab.ui.theme.ManropeFontFamily
import com.example.droidlab.ui.theme.SpaceGroteskFontFamily
import com.example.droidlab.ui.theme.SystemFontFamily
import com.example.droidlab.ui.viewmodel.MusicViewModel
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

    val context = LocalContext.current
    val app = context.applicationContext as MusicApplication
    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModel.provideFactory(app.container)
    )
    val scanState by musicViewModel.scanState.collectAsStateWithLifecycle()
    var showScanFoldersDialog by remember { mutableStateOf(false) }

    // Real Media3 PlayerController
    val playerController = app.container.playerController
    val currentSongEntity by playerController.currentSong.collectAsStateWithLifecycle()
    val isPlaying by playerController.isPlaying.collectAsStateWithLifecycle()
    val positionMs by playerController.positionMs.collectAsStateWithLifecycle()
    val durationMs by playerController.durationMs.collectAsStateWithLifecycle()
    val shuffleEnabled by playerController.shuffleEnabled.collectAsStateWithLifecycle()
    val repeatMode by playerController.repeatMode.collectAsStateWithLifecycle()
    val currentQueue by playerController.queue.collectAsStateWithLifecycle()
    val currentFormatDescription by playerController.currentFormatDescription.collectAsStateWithLifecycle()

    val currentSongTitle = currentSongEntity?.title ?: "Kyoto Garden"
    val currentSongArtist = currentSongEntity?.artist ?: "Zen Echoes"
    val currentSongAlbum = currentSongEntity?.album ?: "Zen Echoes"

    val progressPercent = if (durationMs > 0) ((positionMs.toFloat() / durationMs.toFloat()) * 100).toInt().coerceIn(0, 100) else 0
    val progressFraction = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val currentMins = (positionMs / 1000) / 60
    val currentSecs = (positionMs / 1000) % 60
    val currentTimeString = "%d:%02d".format(currentMins, currentSecs)

    val totalMins = (durationMs / 1000) / 60
    val totalSecs = (durationMs / 1000) % 60
    val totalTimeString = "%d:%02d".format(totalMins, totalSecs)

    // Notification Permission Flow (API 33+)
    var showNotificationExplanation by remember { mutableStateOf(false) }
    var hasRequestedNotificationOnce by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Playback works regardless of whether notification permission is granted or denied
    }

    val requestNotificationPermissionIfNeeded: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasRequestedNotificationOnce) {
            hasRequestedNotificationOnce = true
            val hasNotif = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!hasNotif) {
                showNotificationExplanation = true
            }
        }
    }

    // Audio Permission Flow (themed explanation first, then system dialog; if permanently denied, show Open Settings)
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var showPermissionExplanation by remember { mutableStateOf(false) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showPermissionExplanation = false
            // When permission is granted for the first time, start the first scan automatically
            musicViewModel.startScan()
        } else {
            isPermanentlyDenied = true
            showPermissionExplanation = true
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            showPermissionExplanation = true
        } else {
            musicViewModel.startScan()
        }
    }

    LaunchedEffect(Unit) {
        playerController.playbackErrorEvent.collect { errorMessage ->
            android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    var isNowPlayingOpen by remember { mutableStateOf(false) }
    var isEqualizerOpen by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf<Int?>(null) }
    var selectedMiniPlayerStyle by remember { mutableStateOf("Classic Pill") }
    var selectedAlbumDetail by remember { mutableStateOf<AlbumItem?>(null) }
    var selectedArtistDetail by remember { mutableStateOf<ArtistItem?>(null) }

    // Connected Audio Device / Hardware Profile (reflected in player and throughout app)
    var activeDevicePreset by remember { mutableStateOf<DeviceHardwarePreset>(AvailableDevicePresets.first()) }

    // Shared list of liked songs
    val likedSongs = remember {
        mutableStateListOf(
            Pair("Kyoto Garden", "Zen Echoes"),
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

    // 4 Bottom Tabs: Home, Music, Playlist, Keeps (Dedicated Search tab removed from bar)
    val bottomNavIndex = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.Music.route -> 1
        Screen.Playlist.route -> 2
        Screen.Keep.route -> 3
        else -> -1
    }

    // Floating Mini Player shown at Home, Music, and Playlist pages when full player is closed
    val showMiniPlayer = (currentRoute == Screen.Home.route || currentRoute == Screen.Music.route || currentRoute == Screen.Playlist.route) && !isNowPlayingOpen

    val navigateToSearch: () -> Unit = {
        if (currentRoute != Screen.Search.route) {
            navController.navigate(Screen.Search.route)
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
        // Root container ensuring full-screen overlays sit on top of everything
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    // Hide bottom nav bar completely when full music player, equalizer, or search is active
                    if (bottomNavIndex >= 0 && !isNowPlayingOpen && !isEqualizerOpen) {
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
                    AppNavHost(
                        navController = navController,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenSearch = navigateToSearch,
                        onPlaySongEntity = { song, list ->
                            requestNotificationPermissionIfNeeded()
                            playerController.playSong(song, list)
                        },
                        onSongSelected = { title, artist ->
                            requestNotificationPermissionIfNeeded()
                            if (currentSongEntity?.title.equals(title, ignoreCase = true)) {
                                playerController.playPause()
                            } else {
                                val found = currentQueue.find { it.title.equals(title, ignoreCase = true) }
                                if (found != null) {
                                    playerController.playSong(found, currentQueue)
                                } else {
                                    playerController.playPause()
                                }
                            }
                            isNowPlayingOpen = true
                        },
                        onPlaySong = { title, artist, openFullPlayer ->
                            requestNotificationPermissionIfNeeded()
                            if (currentSongEntity?.title.equals(title, ignoreCase = true)) {
                                playerController.playPause()
                            } else {
                                val found = currentQueue.find { it.title.equals(title, ignoreCase = true) }
                                if (found != null) {
                                    playerController.playSong(found, currentQueue)
                                } else {
                                    playerController.playPause()
                                }
                            }
                            if (openFullPlayer) {
                                isNowPlayingOpen = true
                            }
                        },
                        currentSongTitle = currentSongTitle,
                        isPlaying = isPlaying,
                        likedSongs = likedSongs,
                        onToggleLike = toggleLike,
                        activeDeviceId = activeDevicePreset.id,
                        onDeviceSelected = { activeDevicePreset = it },
                        selectedFontPreference = selectedFontPreference,
                        onFontPreferenceChanged = onFontPreferenceChanged,
                        selectedMiniPlayerStyle = selectedMiniPlayerStyle,
                        onMiniPlayerStyleChanged = { selectedMiniPlayerStyle = it },
                        onAlbumSelected = { album -> selectedAlbumDetail = album },
                        onArtistSelected = { artist -> selectedArtistDetail = artist },
                        musicViewModel = musicViewModel,
                        onScanRequested = { showScanFoldersDialog = true },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Floating Mini Player: at Home, Music, and Playlist pages
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
                                progressPercent = progressPercent,
                                isPlaying = isPlaying,
                                isLiked = isCurrentSongLiked,
                                onToggleLike = { toggleLike(currentSongTitle, currentSongArtist) },
                                onPlayPause = { playerController.playPause() },
                                onNext = { playerController.next() },
                                style = selectedMiniPlayerStyle,
                                albumId = currentSongEntity?.albumId,
                                contentUri = currentSongEntity?.contentUriString,
                                onClick = { isNowPlayingOpen = true }
                            )
                        }
                    }
                }
            }

            // True Full-Screen Now Playing Experience (placed on top of Scaffold in root Box)
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
                    album = currentSongAlbum,
                    progress = progressFraction,
                    currentTime = currentTimeString,
                    totalTime = totalTimeString,
                    isPlaying = isPlaying,
                    isLiked = isCurrentSongLiked,
                    activeDeviceName = activeDevicePreset.name,
                    activeDeviceIcon = activeDevicePreset.iconName,
                    onOpenEqualizer = { isEqualizerOpen = true },
                    onToggleLike = { toggleLike(currentSongTitle, currentSongArtist) },
                    onPlayPause = { playerController.playPause() },
                    onPrevious = { playerController.previous() },
                    onNext = { playerController.next() },
                    albumId = currentSongEntity?.albumId,
                    contentUri = currentSongEntity?.contentUriString,
                    onSongSelected = { title, artist ->
                        val found = currentQueue.find { it.title.equals(title, ignoreCase = true) }
                        if (found != null) {
                            playerController.playSong(found, currentQueue)
                        } else {
                            playerController.playPause()
                        }
                    },
                    onClose = { isNowPlayingOpen = false },
                    onGoToArtist = { artistName ->
                        selectedArtistDetail = ArtistItem(1, artistName, 2, 14)
                    },
                    onGoToAlbum = { albumTitle, artistName ->
                        selectedAlbumDetail = AlbumItem(1, albumTitle, artistName, 8, "2024")
                    },
                    onOpenSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    sleepTimerMinutes = sleepTimerMinutes,
                    onSetSleepTimer = { mins -> sleepTimerMinutes = mins },
                    isShuffleOn = shuffleEnabled,
                    onToggleShuffle = { playerController.toggleShuffle() },
                    isRepeatOn = (repeatMode != androidx.media3.common.Player.REPEAT_MODE_OFF),
                    onToggleRepeat = { playerController.toggleRepeat() },
                    onSeek = { fraction -> playerController.seekTo((fraction * durationMs).toLong()) },
                    audioFormatDescription = currentFormatDescription,
                    queueList = currentQueue,
                    onRemoveFromQueue = { index -> playerController.removeFromQueue(index) },
                    onClearQueue = { playerController.clearQueue() }
                )
            }


            // Audio Permission Dialog
            if (showPermissionExplanation) {
                AudioPermissionDialog(
                    isPermanentlyDenied = isPermanentlyDenied,
                    onGrantClick = {
                        showPermissionExplanation = false
                        permissionLauncher.launch(permissionToRequest)
                    },
                    onDismissRequest = { showPermissionExplanation = false },
                    context = context
                )
            }

            // Playback Notification Permission Dialog (API 33+)
            if (showNotificationExplanation) {
                NotificationPermissionDialog(
                    onGrantClick = {
                        showNotificationExplanation = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onDismissRequest = {
                        showNotificationExplanation = false
                    }
                )
            }

            // Real Scan Music Folders Dialog
            if (showScanFoldersDialog) {
                ScanMusicFoldersDialog(
                    onDismissRequest = { showScanFoldersDialog = false },
                    onStartScan = { folders -> musicViewModel.startScan(folders) },
                    scanProgress = scanState.progress,
                    isScanning = scanState.isScanning,
                    currentFileName = scanState.currentFileName,
                    isScanFinished = scanState.isCompleted,
                    totalFound = scanState.totalFound,
                    errorMessage = scanState.errorMessage
                )
            }

            // Full-Screen Album Detail Overlay
            val activeAlbum = selectedAlbumDetail
            if (activeAlbum != null) {
                AlbumDetailScreen(
                    album = activeAlbum,
                    onBack = { selectedAlbumDetail = null },
                    onSongSelected = { title: String, artist: String ->
                        val found = currentQueue.find { it.title.equals(title, ignoreCase = true) }
                        if (found != null) {
                            playerController.playSong(found, currentQueue)
                        } else {
                            playerController.playPause()
                        }
                    },
                    likedSongs = likedSongs,
                    onToggleLike = toggleLike,
                    currentSongTitle = currentSongTitle,
                    isPlaying = isPlaying,
                    onAlbumSelected = { album: AlbumItem -> selectedAlbumDetail = album }
                )
            }

            // Full-Screen Artist Detail Overlay
            val activeArtist = selectedArtistDetail
            if (activeArtist != null) {
                ArtistDetailScreen(
                    artist = activeArtist,
                    onBack = { selectedArtistDetail = null },
                    onSongSelected = { title: String, artist: String ->
                        val found = currentQueue.find { it.title.equals(title, ignoreCase = true) }
                        if (found != null) {
                            playerController.playSong(found, currentQueue)
                        } else {
                            playerController.playPause()
                        }
                    },
                    likedSongs = likedSongs,
                    onToggleLike = toggleLike,
                    currentSongTitle = currentSongTitle,
                    isPlaying = isPlaying,
                    onAlbumSelected = { album: AlbumItem -> selectedAlbumDetail = album }
                )
            }

            // Full-screen Equalizer View (placed on top of Scaffold in root Box)
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
