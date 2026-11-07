package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar

data class DownloadingEntry(
    val id: Int,
    val filename: String,
    val progress: Float,
    val downloadedSize: String,
    val totalSize: String,
    val leftSize: String,
    val etaFormatted: String, // ONLY minute & hour, NO seconds (e.g. "1 min", "2 min", "1 hr 10 min")
    val speed: String,
    val isPaused: Boolean,
    val isFailed: Boolean = false
)

data class DownloadedEntry(
    val id: Int,
    val filename: String,
    val format: String,
    val size: String,
    val duration: String
)

data class FinishedEntry(
    val id: Int,
    val filename: String,
    val trackCount: String,
    val totalSize: String,
    val date: String
)

/**
 * Keeps Screen (Downloads Manager):
 * - Unified single large rounded card for downloads queue with interior subtle shading (feels like 1 part)
 * - Clean progress bar and pause/resume controls
 * - Displays downloaded size and file left
 * - Time formatted in minutes and hours only (no seconds)
 * - Speed shown (or "Paused")
 * - Slate/monochrome theme matching the application UI
 * - Tabs: "Downloading", "Downloaded", "Finished"
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeepsRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    val isDark = isSystemInDarkTheme()
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
                DownloadingEntry(1, "Neon.Horizon.Lossless.FLAC.mkv", 0.33f, "530 MB", "1.6 GB", "1.07 GB left", "1 min", "12.8 Mb/s", false),
                DownloadingEntry(2, "Kyoto.Garden.Zen.Echoes.flac", 0.68f, "18.4 MB", "27.0 MB", "8.6 MB left", "1 min", "8.4 Mb/s", false),
                DownloadingEntry(3, "Solar.Drift.Cosmic.Sound.Subtitle.zip", 0.45f, "4.2 MB", "9.1 MB", "4.9 MB left", "2 min", "Paused", true),
                DownloadingEntry(4, "Chillhop.Essentials.2024.zip", 0.0f, "0 MB", "180 MB", "180 MB left", "--", "Failed", false, isFailed = true)
            )
        )
    }

    var downloadedList by remember {
        mutableStateOf(
            listOf(
                DownloadedEntry(101, "Silo.S01E07.720p.Subtitle.zip", "ZIP Archive", "1.4 MB", "Added Today"),
                DownloadedEntry(102, "Midnight_Drive_Master_24bit.flac", "FLAC 24-bit", "28.4 MB", "4:12"),
                DownloadedEntry(103, "Blue_Bottle_Coffee_LoFi.mp3", "MP3 320kbps", "8.6 MB", "3:45"),
                DownloadedEntry(104, "The_Reading_Room_Ambient.flac", "FLAC 24-bit", "22.1 MB", "3:30"),
                DownloadedEntry(105, "Golden_Hour_Acoustic.mp3", "MP3 320kbps", "7.5 MB", "3:15")
            )
        )
    }

    var finishedList by remember {
        mutableStateOf(
            listOf(
                FinishedEntry(201, "Neon_Hours_Complete_Album.zip", "10 tracks", "142 MB", "Today at 14:22"),
                FinishedEntry(202, "Chillhop_Essentials_Discography.zip", "14 tracks", "186 MB", "Yesterday"),
                FinishedEntry(203, "Zen_Echoes_Special_Edition.flac", "6 tracks", "94 MB", "Oct 28")
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

            val cardBg = if (isDark) Color(0xFF1B1E26) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)

            when (selectedTabIndex) {
                0 -> {
                    // "Downloading" Tab: One large rounded box with interior subtle shades (feels like 1 part)
                    val activeDownloads = downloadingList.filter { !it.isFailed }
                    val allPaused = activeDownloads.isNotEmpty() && activeDownloads.all { it.isPaused }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        item {
                            // One unified large rounded corner box
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Header inside the large card
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "download", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                                            }

                                            Spacer(Modifier.width(10.dp))

                                            Column {
                                                Text(
                                                    text = "Downloading Queue",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${activeDownloads.size} active • 552.6 MB of 1.8 GB (1.25 GB left) • 1 min • 21.2 Mb/s",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Pause/Resume All Button
                                        IconButton(
                                            onClick = {
                                                val shouldPause = !allPaused
                                                downloadingList = downloadingList.map {
                                                    if (!it.isFailed) it.copy(isPaused = shouldPause) else it
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            AppIcon(
                                                name = if (allPaused) "play" else "pause",
                                                size = 20.dp,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Entries inside the single large card, with subtle shade to differentiate
                                    downloadingList.forEachIndexed { index, item ->
                                        if (index > 0) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                                                thickness = 0.75.dp
                                            )
                                        }

                                        val entryShade = if (index % 2 == 1) {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        } else {
                                            Color.Transparent
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(entryShade)
                                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AppIcon(
                                                        name = if (item.isFailed) "close" else "music",
                                                        size = 15.dp,
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Spacer(Modifier.width(10.dp))

                                                Text(
                                                    text = item.filename,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (item.isFailed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                Spacer(Modifier.width(6.dp))

                                                if (!item.isFailed) {
                                                    IconButton(
                                                        onClick = {
                                                            downloadingList = downloadingList.map {
                                                                if (it.id == item.id) it.copy(isPaused = !it.isPaused) else it
                                                            }
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        AppIcon(
                                                            name = if (item.isPaused) "play" else "pause",
                                                            size = 17.dp,
                                                            tint = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                } else {
                                                    IconButton(
                                                        onClick = {
                                                            downloadingList = downloadingList.map {
                                                                if (it.id == item.id) it.copy(isFailed = false, isPaused = false, progress = 0.05f, speed = "6.4 Mb/s", etaFormatted = "2 min") else it
                                                            }
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        AppIcon(name = "refresh", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                                                    }
                                                }

                                                Spacer(Modifier.width(4.dp))

                                                IconButton(
                                                    onClick = {
                                                        downloadingList = downloadingList.filter { it.id != item.id }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    AppIcon(name = "close", size = 17.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }

                                            if (!item.isFailed) {
                                                Spacer(Modifier.height(8.dp))

                                                // Progress Bar
                                                LinearProgressIndicator(
                                                    progress = { item.progress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp)),
                                                    color = if (item.isPaused) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                                )

                                                Spacer(Modifier.height(7.dp))

                                                // Metadata: Downloaded size & left, time (minutes & hours only, no seconds), speed or paused
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${item.downloadedSize} of ${item.totalSize} (${item.leftSize})",
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )

                                                    Text(
                                                        text = if (item.isPaused) "Paused" else "${item.etaFormatted} • ${item.speed}",
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            } else {
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    text = "Download failed • Tap refresh to retry",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // "Downloaded" Tab: Completed entries matching theme
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(downloadedList, key = { it.id }) { item ->
                            val isSelected = item.id in selectedItemIds

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    } else {
                                        cardBg
                                    }
                                ),
                                border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
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
                                                onSongSelected(item.filename, "Downloaded Track")
                                            }
                                        },
                                        onLongClick = {
                                            isSelectionMode = true
                                            selectedItemIds = selectedItemIds + item.id
                                        }
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                    }

                                    // File glyph (theme neutral)
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "music", size = 16.dp, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.filename,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.format} • ${item.size} • ${item.duration}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Play / Open
                                    IconButton(
                                        onClick = {
                                            onSongSelected(item.filename, "Downloaded Track")
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        AppIcon(name = "library_music", size = 19.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Spacer(Modifier.width(4.dp))

                                    // Delete / Close Icon (✕)
                                    IconButton(
                                        onClick = {
                                            downloadedList = downloadedList.filter { it.id != item.id }
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Finished" Tab: Completed batch downloads matching theme
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(finishedList, key = { it.id }) { batch ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "check", size = 18.dp, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = batch.filename,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${batch.trackCount} • ${batch.totalSize} • ${batch.date}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            finishedList = finishedList.filter { it.id != batch.id }
                                        },
                                        modifier = Modifier.size(30.dp)
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
