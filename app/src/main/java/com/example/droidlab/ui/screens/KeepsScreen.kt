package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
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
    val eta: String,
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
 * Keeps Screen transformed into an offline Downloads Manager matching exact visual reference:
 * - Green progress bar, amber percentage, size/ETA/speed pill badges.
 * - Completed entries with folder action, failed entries with retry action.
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
                DownloadingEntry(1, "Neon.Horizon.Lossless.FLAC.mkv", 0.32f, "530 MB", "1.6 GB", "00:00:32", "12.8 Mb/s", false),
                DownloadingEntry(2, "Kyoto.Garden.Zen.Echoes.flac", 0.68f, "18.4 MB", "27.0 MB", "00:00:15", "8.4 Mb/s", false),
                DownloadingEntry(3, "Solar.Drift.Cosmic.Sound.Subtitle.zip", 0.45f, "4.2 MB", "9.1 MB", "--:--:--", "Paused", true),
                DownloadingEntry(4, "Chillhop.Essentials.2024.zip", 0.0f, "0 MB", "180 MB", "--:--:--", "Failed", false, isFailed = true)
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
                // Contextual selection top bar
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

                        // Select All
                        TextButton(onClick = {
                            selectedItemIds = downloadedList.map { it.id }.toSet()
                        }) {
                            Text("Select All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Delete Selected
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

            val cardBg = if (isDark) Color(0xFF1B1E26) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            val pillBg = if (isDark) Color(0xFF232732) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            val pillBorder = BorderStroke(1.dp, if (isDark) Color(0xFF2E3442) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            when (selectedTabIndex) {
                0 -> {
                    // "Downloading" Tab matching exact reference design
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(downloadingList, key = { it.id }) { item ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    // Top Row: File icon + Filename + (Percent & Actions)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left File Glyph (Purple/Indigo for video/audio, Gray for failed)
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (item.isFailed) Color(0xFF4B5563) else Color(0xFF5B68DF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (item.isFailed) {
                                                Text("?", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            } else {
                                                AppIcon(name = "play", size = 15.dp, tint = Color.White)
                                            }
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        // Filename
                                        Text(
                                            text = item.filename,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (item.isFailed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(Modifier.width(8.dp))

                                        if (!item.isFailed) {
                                            // Amber / Yellow percentage
                                            Text(
                                                text = "${(item.progress * 100).toInt()}%",
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFBBF24)
                                            )

                                            Spacer(Modifier.width(12.dp))

                                            // Pause / Resume icon (|| or ▷)
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
                                                    size = 18.dp,
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Spacer(Modifier.width(6.dp))
                                        } else {
                                            // Retry action for failed entry
                                            IconButton(
                                                onClick = {
                                                    downloadingList = downloadingList.map {
                                                        if (it.id == item.id) it.copy(isFailed = false, isPaused = false, progress = 0.05f, speed = "6.4 Mb/s") else it
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                AppIcon(name = "refresh", size = 19.dp, tint = MaterialTheme.colorScheme.primary)
                                            }

                                            Spacer(Modifier.width(6.dp))
                                        }

                                        // Cancel / Close icon (✕)
                                        IconButton(
                                            onClick = {
                                                downloadingList = downloadingList.filter { it.id != item.id }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    if (!item.isFailed) {
                                        Spacer(Modifier.height(10.dp))

                                        // Middle Row: Vibrant Green Progress Bar matching screenshot
                                        LinearProgressIndicator(
                                            progress = { item.progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.5.dp)
                                                .clip(RoundedCornerShape(99.dp)),
                                            color = if (item.isPaused) Color(0xFF6B7280) else Color(0xFF22C55E),
                                            trackColor = if (isDark) Color(0xFF2E333F) else MaterialTheme.colorScheme.surfaceVariant
                                        )

                                        Spacer(Modifier.height(10.dp))

                                        // Bottom Row: Size pill, ETA pill, Speed pill
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Pill 1: Downloaded size
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(99.dp))
                                                    .border(pillBorder.width, pillBorder.brush, RoundedCornerShape(99.dp))
                                                    .background(pillBg)
                                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AppIcon(name = "download", size = 12.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = item.downloadedSize,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            // Pill 2: ETA time
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(99.dp))
                                                    .border(pillBorder.width, pillBorder.brush, RoundedCornerShape(99.dp))
                                                    .background(pillBg)
                                                    .padding(horizontal = 14.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = item.eta,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Pill 3: Vibrant blue speed pill with wifi/speed icon
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(99.dp))
                                                    .background(if (item.isPaused) pillBg else Color(0xFF2563EB))
                                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AppIcon(
                                                    name = "speed",
                                                    size = 13.dp,
                                                    tint = if (item.isPaused) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                                )
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = item.speed,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (item.isPaused) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
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
                    // "Downloaded" Tab matching completed entry style (Card 2 in screenshot)
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

                                    // Zip / Audio file badge (Blue)
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF3B82F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "music", size = 16.dp, tint = Color.White)
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

                                    // Folder Icon (📁)
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
                    // "Finished" Tab: Completed batch downloads
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
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "check", size = 18.dp, tint = Color.White)
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
