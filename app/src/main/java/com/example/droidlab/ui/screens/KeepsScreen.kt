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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar

data class DownloadingItem(
    val id: Int,
    val title: String,
    val artist: String,
    val progress: Float,
    val downloadedSize: String,
    val totalSize: String,
    val speed: String,
    val isPaused: Boolean
)

data class DownloadedItem(
    val id: Int,
    val title: String,
    val artist: String,
    val format: String,
    val size: String,
    val duration: String
)

data class FinishedBatch(
    val id: Int,
    val title: String,
    val trackCount: String,
    val totalSize: String,
    val date: String
)

/**
 * Keeps Screen transformed into an offline Downloads Manager (.screen-page[data-page-label="Keep"]):
 * - Tabs: "Downloading", "Downloaded", "Finished"
 * - Multi-select mode with hold-to-select & delete all
 * - Minimal download progress sliders and speed indicators
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
                DownloadingItem(1, "Neon Horizon", "Retro Wave", 0.68f, "5.4 MB", "8.2 MB", "1.8 MB/s", false),
                DownloadingItem(2, "Kyoto Garden", "Zen Echoes", 0.32f, "2.6 MB", "7.8 MB", "2.1 MB/s", false),
                DownloadingItem(3, "Solar Drift", "Cosmic Sound", 0.05f, "0.4 MB", "9.1 MB", "Queued", true)
            )
        )
    }

    var downloadedList by remember {
        mutableStateOf(
            listOf(
                DownloadedItem(101, "Midnight Drive", "Nova Lane", "FLAC 24-bit", "28.4 MB", "4:12"),
                DownloadedItem(102, "Blue Bottle Coffee", "Chillhop Beats", "MP3 320kbps", "8.6 MB", "3:45"),
                DownloadedItem(103, "The Reading Room", "Ambient Works", "FLAC 24-bit", "22.1 MB", "3:30"),
                DownloadedItem(104, "Golden Hour", "Acoustic Sunset", "MP3 320kbps", "7.5 MB", "3:15")
            )
        )
    }

    var finishedList by remember {
        mutableStateOf(
            listOf(
                FinishedBatch(201, "Neon Hours (Album)", "10 tracks", "142 MB", "Finished Today at 14:22"),
                FinishedBatch(202, "Chillhop Essentials", "14 tracks", "186 MB", "Finished Yesterday"),
                FinishedBatch(203, "Zen Echoes Special Edition", "6 tracks", "94 MB", "Finished Oct 28")
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
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedItemIds = emptySet()
                        }) {
                            AppIcon(name = "close", size = 22.dp)
                        }

                        Text(
                            text = "${selectedItemIds.size} selected",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )

                        TextButton(onClick = {
                            selectedItemIds = downloadedList.map { it.id }.toSet()
                        }) {
                            Text("Select All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = {
                            downloadedList = downloadedList.filterNot { it.id in selectedItemIds }
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
                            "Pause All" -> downloadingList = downloadingList.map { it.copy(isPaused = true) }
                            "Resume All" -> downloadingList = downloadingList.map { it.copy(isPaused = false) }
                            "Delete All Finished" -> finishedList = emptyList()
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
                    // "Downloading" Tab: Active downloads with minimal sliders & speed
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            // Quick control header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active Downloads (${downloadingList.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = {
                                            downloadingList = downloadingList.map { it.copy(isPaused = !it.isPaused) }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (downloadingList.any { !it.isPaused }) "Pause All" else "Resume All",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        if (downloadingList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No active downloads", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(downloadingList, key = { it.id }) { dl ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(
                                                    name = "download",
                                                    size = 20.dp,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }

                                            Spacer(Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = dl.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = dl.artist,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }

                                            // Pause / Resume icon button
                                            IconButton(
                                                onClick = {
                                                    downloadingList = downloadingList.map {
                                                        if (it.id == dl.id) it.copy(isPaused = !it.isPaused) else it
                                                    }
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                AppIcon(
                                                    name = if (dl.isPaused) "play" else "pause",
                                                    size = 20.dp,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            // Cancel button
                                            IconButton(
                                                onClick = {
                                                    downloadingList = downloadingList.filterNot { it.id == dl.id }
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                AppIcon(name = "close", size = 18.dp)
                                            }
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        // Minimal Progress Slider / Bar
                                        LinearProgressIndicator(
                                            progress = { dl.progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (dl.isPaused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        // Size & Speed info
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${dl.downloadedSize} / ${dl.totalSize} • ${(dl.progress * 100).toInt()}%",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = if (dl.isPaused) "Paused" else dl.speed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (dl.isPaused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // "Downloaded" Tab: Offline songs with hold-to-select multi-selection
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${downloadedList.size} Tracks Saved Offline",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!isSelectionMode) {
                                    TextButton(onClick = { isSelectionMode = true }) {
                                        Text("Select", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        items(downloadedList, key = { it.id }) { item ->
                            val isSelected = item.id in selectedItemIds

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (isSelectionMode) {
                                                selectedItemIds = if (isSelected) {
                                                    selectedItemIds - item.id
                                                } else {
                                                    selectedItemIds + item.id
                                                }
                                            } else {
                                                onSongSelected(item.title, item.artist)
                                            }
                                        },
                                        onLongClick = {
                                            isSelectionMode = true
                                            selectedItemIds = selectedItemIds + item.id
                                        }
                                    ),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                            },
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(name = "check", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.artist} • ${item.format} • ${item.size}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }

                                    Text(
                                        text = item.duration,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Finished" Tab: Completed albums & batch downloads history
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Download History",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (finishedList.isNotEmpty()) {
                                    TextButton(onClick = { finishedList = emptyList() }) {
                                        Text("Clear All", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        if (finishedList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No download history", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(finishedList, key = { it.id }) { batch ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(name = "library_music", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                                        }

                                        Spacer(Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = batch.title,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${batch.trackCount} • ${batch.totalSize}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = batch.date,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
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
}
