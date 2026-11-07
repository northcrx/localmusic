package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
    val isPaused: Boolean,
    val isFailed: Boolean = false
)

data class DownloadedEntry(
    val id: Int,
    val title: String,
    val artist: String,
    val format: String,
    val size: String,
    val duration: String
)

data class FinishedEntry(
    val id: Int,
    val title: String,
    val trackCount: String,
    val totalSize: String,
    val date: String
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
 * - Entries styled to match Music "For you" section:
 *   - 46dp square box with subtle rounded corners (6.dp) displaying music artwork
 *   - Music titles limited to 1 or 2 words (larger titles show ellipsis "...")
 *   - Metadata displays music size and time it takes (no download speed or percentages)
 *   - Minimal progress bar with pause/resume and close actions
 * - Tabs: "Downloading", "Downloaded", "Finished"
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeepsRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Downloading", "Downloaded", "Finished")

    val keepsMenuItems = remember {
        listOf(
            Pair("Pause All", "pause"),
            Pair("Resume All", "play"),
            Pair("Delete All Finished", "delete"),
            Pair("Storage Settings", "settings")
        )
    }

    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedItemIds by remember { mutableStateOf(setOf<Int>()) }

    var downloadingList by remember {
        mutableStateOf(
            listOf(
                DownloadingEntry(1, "Kyoto Garden Zen Echoes", "Zen Echoes", 0.68f, "18.4 MB", "27.0 MB", "1 min", false),
                DownloadingEntry(2, "Neon Horizon", "Retro Wave", 0.33f, "530 MB", "1.6 GB", "1 min", false),
                DownloadingEntry(3, "Solar Drift Cosmic Sound", "Cosmic Sound", 0.45f, "4.2 MB", "9.1 MB", "2 min", true),
                DownloadingEntry(4, "Chillhop Essentials Collection", "Chillhop Beats", 0.0f, "0 MB", "180 MB", "--", false, isFailed = true)
            )
        )
    }

    var downloadedList by remember {
        mutableStateOf(
            listOf(
                DownloadedEntry(101, "Kyoto Garden", "Zen Echoes", "FLAC 24-bit", "27.0 MB", "2:58"),
                DownloadedEntry(102, "Midnight Drive", "Nova Lane", "FLAC 24-bit", "28.4 MB", "4:12"),
                DownloadedEntry(103, "Blue Bottle Coffee", "Chillhop Beats", "MP3 320kbps", "8.6 MB", "3:45"),
                DownloadedEntry(104, "The Reading Room", "Ambient Works", "FLAC 24-bit", "22.1 MB", "3:30"),
                DownloadedEntry(105, "Golden Hour", "Acoustic Sunset", "MP3 320kbps", "7.5 MB", "3:15")
            )
        )
    }

    var finishedList by remember {
        mutableStateOf(
            listOf(
                FinishedEntry(201, "Zen Echoes Special Edition", "6 tracks", "94 MB", "Oct 28"),
                FinishedEntry(202, "Neon Hours Complete Album", "10 tracks", "142 MB", "Today at 14:22"),
                FinishedEntry(203, "Chillhop Essentials Discography", "14 tracks", "186 MB", "Yesterday")
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

                        TextButton(onClick = {
                            selectedItemIds = downloadedList.map { it.id }.toSet()
                        }) {
                            Text("Select All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = {
                            downloadedList = downloadedList.filter { it.id !in selectedItemIds }
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
                    menuItems = keepsMenuItems,
                    onActionClick = { action ->
                        when (action) {
                            "Pause All" -> {
                                downloadingList = downloadingList.map { it.copy(isPaused = true) }
                            }
                            "Resume All" -> {
                                downloadingList = downloadingList.map { it.copy(isPaused = false) }
                            }
                            "Delete All Finished" -> {
                                finishedList = emptyList()
                            }
                        }
                    }
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
                    // "Downloading" Tab matching Music "For you" styling
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
                                        name = "download",
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
                                            .background(
                                                if (item.isFailed) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isZenArtwork && !item.isFailed) {
                                            KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                        } else {
                                            AppIcon(
                                                name = if (item.isFailed) "close" else "music",
                                                size = 22.dp,
                                                tint = if (item.isFailed) MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.primary
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
                                            color = if (item.isFailed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (item.isFailed) {
                                            Spacer(Modifier.height(3.dp))
                                            Text(
                                                text = "Failed to download",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.error,
                                                maxLines = 1
                                            )
                                        } else {
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
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    // 3. Trailing Action buttons
                                    if (item.isFailed) {
                                        IconButton(
                                            onClick = {
                                                downloadingList = downloadingList.map {
                                                    if (it.id == item.id) it.copy(isFailed = false, isPaused = false, progress = 0.05f, etaFormatted = "2 min") else it
                                                }
                                            },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            AppIcon(name = "refresh", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    } else {
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
                    // "Downloaded" Tab: Flat list entries with Music artwork
                    if (downloadedList.isEmpty()) {
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
                                    text = "No Downloaded Files",
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
                            items(downloadedList, key = { it.id }) { item ->
                                val isSelected = item.id in selectedItemIds
                                val displayName = formatMusicTitle(item.title)
                                val isZenArtwork = item.title.contains("Kyoto", ignoreCase = true) || item.title.contains("Zen", ignoreCase = true)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                            else Color.Transparent
                                        )
                                        .combinedClickable(
                                            onClick = {
                                                if (isSelectionMode) {
                                                    selectedItemIds = if (isSelected) {
                                                        selectedItemIds - item.id
                                                    } else {
                                                        selectedItemIds + item.id
                                                    }
                                                    if (selectedItemIds.isEmpty()) isSelectionMode = false
                                                } else {
                                                    onSongSelected(item.title, item.artist)
                                                }
                                            },
                                            onLongClick = {
                                                isSelectionMode = true
                                                selectedItemIds = selectedItemIds + item.id
                                            }
                                        )
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelectionMode) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                selectedItemIds = if (checked) {
                                                    selectedItemIds + item.id
                                                } else {
                                                    selectedItemIds - item.id
                                                }
                                                if (selectedItemIds.isEmpty()) isSelectionMode = false
                                            },
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                    }

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

                                    // Play / Open
                                    IconButton(
                                        onClick = {
                                            onSongSelected(item.title, item.artist)
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        AppIcon(name = "library_music", size = 19.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    // Delete / Close Icon
                                    IconButton(
                                        onClick = {
                                            downloadedList = downloadedList.filter { it.id != item.id }
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
                    // "Finished" Tab: Flat list entries with Music artwork
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
                                        name = "check",
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No Finished Batches",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Completed music albums will appear here.",
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
                            items(finishedList, key = { it.id }) { batch ->
                                val displayName = formatMusicTitle(batch.title)
                                val isZenArtwork = batch.title.contains("Kyoto", ignoreCase = true) || batch.title.contains("Zen", ignoreCase = true)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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
                                            AppIcon(name = "album", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
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
                                            text = "${batch.trackCount} • ${batch.totalSize} • ${batch.date}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            finishedList = finishedList.filter { it.id != batch.id }
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
}
