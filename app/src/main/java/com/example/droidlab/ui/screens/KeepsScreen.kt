package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.KyotoGardenArt

data class DownloadingEntry(
    val id: Int,
    val title: String,
    val artist: String,
    val progress: Float,
    val downloadedSize: String,
    val totalSize: String,
    val etaFormatted: String, // ONLY minute & hour, NO seconds (e.g. "1 min", "2 min")
    val isPaused: Boolean
)

data class FailedEntry(
    val id: Int,
    val title: String,
    val artist: String,
    val progress: Float,
    val downloadedSize: String,
    val totalSize: String,
    val errorCode: String,
    val errorMessage: String,
    val errorDetails: String
)

data class FinishedEntry(
    val id: Int,
    val title: String,
    val artist: String,
    val format: String,
    val size: String,
    val duration: String
)

/**
 * Formats song titles: maximum of 1 or 2 words; if longer than 2 words, appends "..."
 */
fun formatMusicTitle(name: String): String {
    val clean = name.replace(Regex("\\.(mkv|flac|mp3|zip|rar|7z)$", RegexOption.IGNORE_CASE), "")
        .replace(".", " ")
        .replace("_", " ")
        .trim()
    val words = clean.split(Regex("\\s+")).filter { it.isNotBlank() }
    return if (words.size > 2) {
        "${words[0]} ${words[1]}..."
    } else {
        clean
    }
}

/**
 * Keeps Screen (Downloads Manager):
 * - 3 Distinct Tabs: "Downloading", "Failed", "Finished"
 * - Entries styled to match Music "For you" section:
 *   - 46dp square box with subtle rounded corners (6.dp) displaying authentic music artwork
 *   - Failed items display normal artwork (no cross icon) + red text and stopped progress
 *   - Diagnostic info button on failed downloads displaying server/timeout details & copy-to-clipboard
 *   - Music titles limited to 1 or 2 words (larger titles append "...")
 *   - Finished tab has unnecessary music button removed; row click plays song, close deletes
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeepsRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Downloading", "Failed", "Finished")

    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedItemIds by remember { mutableStateOf(setOf<Int>()) }

    var selectedFailedError by remember { mutableStateOf<FailedEntry?>(null) }

    var downloadingList by remember {
        mutableStateOf(
            listOf(
                DownloadingEntry(1, "Kyoto Garden Zen Echoes", "Zen Echoes", 0.68f, "18.4 MB", "27.0 MB", "1 min", false),
                DownloadingEntry(2, "Neon Horizon", "Retro Wave", 0.33f, "530 MB", "1.6 GB", "1 min", false),
                DownloadingEntry(3, "Solar Drift Cosmic Sound", "Cosmic Sound", 0.45f, "4.2 MB", "9.1 MB", "2 min", true)
            )
        )
    }

    var failedList by remember {
        mutableStateOf(
            listOf(
                FailedEntry(
                    id = 101,
                    title = "Chillhop Essentials Collection",
                    artist = "Chillhop Beats",
                    progress = 0.42f,
                    downloadedSize = "75.6 MB",
                    totalSize = "180 MB",
                    errorCode = "ERR_HTTP_504_GATEWAY_TIMEOUT",
                    errorMessage = "Server Gateway Timeout (HTTP 504)",
                    errorDetails = """[FAIL] Download Worker Thread #2
URI: https://cdn.musicrepo.internal/flac/chillhop_essentials_pack.flac
Status Code: 504 Gateway Timeout
Exception: java.net.SocketTimeoutException: Connection timed out after 30000ms
Headers:
  Server: CloudFront
  X-Cache: Error from CloudFront
Diagnosis: Remote server failed to complete byte-range streaming within the socket timeout threshold.
Client Version: DroidLab LocalMusic Engine v1.4.2"""
                ),
                FailedEntry(
                    id = 102,
                    title = "Ambient Sound Flow",
                    artist = "Ambient Works",
                    progress = 0.18f,
                    downloadedSize = "8.1 MB",
                    totalSize = "45.0 MB",
                    errorCode = "ERR_SSL_HANDSHAKE_FAILURE",
                    errorMessage = "SSL Handshake Failure (TLS Error)",
                    errorDetails = """[FAIL] Download Worker Thread #5
URI: https://audio-us-east.streamcdn.org/raw/ambient_sound_flow.flac
Exception: javax.net.ssl.SSLHandshakeException: Certificate validation failed
Diagnosis: The remote server presented an expired TLS certificate during handshake negotiation.
Client Version: DroidLab LocalMusic Engine v1.4.2"""
                )
            )
        )
    }

    var finishedList by remember {
        mutableStateOf(
            listOf(
                FinishedEntry(201, "Kyoto Garden", "Zen Echoes", "FLAC 24-bit", "27.0 MB", "2:58"),
                FinishedEntry(202, "Midnight Drive", "Nova Lane", "FLAC 24-bit", "28.4 MB", "4:12"),
                FinishedEntry(203, "Blue Bottle Coffee", "Chillhop Beats", "MP3 320kbps", "8.6 MB", "3:45"),
                FinishedEntry(204, "The Reading Room", "Ambient Works", "FLAC 24-bit", "22.1 MB", "3:30"),
                FinishedEntry(205, "Golden Hour", "Acoustic Sunset", "MP3 320kbps", "7.5 MB", "3:15")
            )
        )
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedItemIds = emptySet()
                        }) {
                            AppIcon(name = "close", size = 22.dp)
                        }

                        Text(
                            text = "${selectedItemIds.size} Selected",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        IconButton(onClick = {
                            if (selectedTabIndex == 0) {
                                downloadingList = downloadingList.filter { it.id !in selectedItemIds }
                            } else if (selectedTabIndex == 1) {
                                failedList = failedList.filter { it.id !in selectedItemIds }
                            } else {
                                finishedList = finishedList.filter { it.id !in selectedItemIds }
                            }
                            isSelectionMode = false
                            selectedItemIds = emptySet()
                        }) {
                            AppIcon(name = "delete", size = 22.dp, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            } else {
                DroidLabTopAppBar(
                    title = "Keeps",
                    onMenuClick = onOpenDrawer,
                    onSearchClick = onOpenSearch,
                    onOpenSettings = onOpenSettings,
                    onOpenEqualizer = onOpenEqualizer
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DroidLabTabs(
                tabs = tabs,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            when (selectedTabIndex) {
                0 -> {
                    // "Downloading" Tab: Active and paused downloads
                    if (downloadingList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(
                                        name = "cloud_download",
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No Active Downloads",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Queued downloads will appear here.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                        ) {
                            items(downloadingList, key = { it.id }) { item ->
                                val displayName = formatMusicTitle(item.title)
                                val isZenArtwork = item.title.contains("Kyoto", ignoreCase = true) || item.title.contains("Zen", ignoreCase = true)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Square artwork box with rounded corners (46dp, 6.dp corner radius) matching Music "For you"
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isZenArtwork) {
                                            KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                        } else {
                                            AppIcon(
                                                name = "music",
                                                size = 22.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    // 2. Middle Content: Music name (1-2 words + ...), progress bar, size & time
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = displayName,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(Modifier.height(5.dp))
                                        LinearProgressIndicator(
                                            progress = { item.progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.5.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = if (item.isPaused) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        // Displays music size and time it takes (NO speed, NO percentages)
                                        Text(
                                            text = "${item.downloadedSize} of ${item.totalSize} • ${if (item.isPaused) "Paused" else "${item.etaFormatted} left"}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    // 3. Trailing Action buttons: Play/Pause and Close
                                    IconButton(
                                        onClick = {
                                            downloadingList = downloadingList.map {
                                                if (it.id == item.id) it.copy(isPaused = !it.isPaused) else it
                                            }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(
                                            name = if (item.isPaused) "play" else "pause",
                                            size = 18.dp,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    IconButton(
                                        onClick = {
                                            downloadingList = downloadingList.filter { it.id != item.id }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // "Failed" Tab: Failed downloads with retry, error diagnosis info button, and stopped progress bar
                    if (failedList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(
                                        name = "check_circle",
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No Failed Downloads",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "All your music downloads completed without errors.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                        ) {
                            items(failedList, key = { it.id }) { item ->
                                val displayName = formatMusicTitle(item.title)
                                val isZenArtwork = item.title.contains("Kyoto", ignoreCase = true) || item.title.contains("Zen", ignoreCase = true)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Square artwork box: shows normal music artwork (no cross icon)
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isZenArtwork) {
                                            KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                        } else {
                                            AppIcon(
                                                name = "music",
                                                size = 22.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    // 2. Middle Content: Music name, stopped progress bar in error tone, red status text
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = displayName,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(Modifier.height(5.dp))
                                        // Process bar where it stopped
                                        LinearProgressIndicator(
                                            progress = { item.progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.5.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = MaterialTheme.colorScheme.error,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Failed • ${(item.progress * 100).toInt()}% • ${item.downloadedSize} of ${item.totalSize}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.error,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(Modifier.width(6.dp))

                                    // 3. Trailing Action buttons: Retry, Error Diagnosis Info, Close
                                    IconButton(
                                        onClick = {
                                            failedList = failedList.filter { it.id != item.id }
                                            downloadingList = downloadingList + DownloadingEntry(
                                                id = item.id,
                                                title = item.title,
                                                artist = item.artist,
                                                progress = 0.05f,
                                                downloadedSize = "1.0 MB",
                                                totalSize = item.totalSize,
                                                etaFormatted = "2 min",
                                                isPaused = false
                                            )
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "refresh", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    // Info button: pops up diagnostic failure error modal
                                    IconButton(
                                        onClick = {
                                            selectedFailedError = item
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "info", size = 18.dp, tint = MaterialTheme.colorScheme.error)
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    IconButton(
                                        onClick = {
                                            failedList = failedList.filter { it.id != item.id }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Finished" Tab: Completed music tracks (redundant music note icon removed)
                    if (finishedList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(
                                        name = "music",
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No Finished Downloads",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Completed music files will appear here.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                        ) {
                            items(finishedList, key = { it.id }) { item ->
                                val displayName = formatMusicTitle(item.title)
                                val isZenArtwork = item.title.contains("Kyoto", ignoreCase = true) || item.title.contains("Zen", ignoreCase = true)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSongSelected(item.title, item.artist)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Square artwork box (46dp, 6.dp corner radius)
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isZenArtwork) {
                                            KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                        } else {
                                            AppIcon(
                                                name = "music",
                                                size = 22.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = displayName,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "${item.format} • ${item.size} • ${item.duration}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Redundant music icon removed - only close/delete icon remains
                                    IconButton(
                                        onClick = {
                                            finishedList = finishedList.filter { it.id != item.id }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Download Failure Diagnostic Error Modal
    if (selectedFailedError != null) {
        val clipboardManager = LocalClipboardManager.current
        var isCopied by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedFailedError = null },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "info", size = 22.dp, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Download Failure Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedFailedError!!.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = selectedFailedError!!.errorMessage,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Code: ${selectedFailedError!!.errorCode}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Stopped at ${(selectedFailedError!!.progress * 100).toInt()}% (${selectedFailedError!!.downloadedSize} of ${selectedFailedError!!.totalSize})",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Diagnostic Log:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedFailedError!!.errorDetails,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    if (isCopied) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "✓ Error log copied! Ready to paste into GitHub issues.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(selectedFailedError!!.errorDetails))
                        isCopied = true
                    }
                ) {
                    Text(if (isCopied) "Copied!" else "Copy Error Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFailedError = null }) {
                    Text("Close")
                }
            }
        )
    }
}
