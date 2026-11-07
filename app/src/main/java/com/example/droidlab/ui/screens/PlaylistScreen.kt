package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.droidlab.ui.components.KyotoGardenArt

data class PlaylistItem(
    val id: Int,
    val name: String,
    val trackCount: Int,
    val duration: String,
    val isZenCover: Boolean = false
)

/**
 * Playlist Screen:
 * Replaces the dedicated search tab in bottom navigation.
 * Displays user playlists, favorites, and folder collections matching Droid Lab styling.
 */
@Composable
fun PlaylistRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    likedSongs: List<Pair<String, String>> = emptyList()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Playlists", "Favorites", "Folders")

    val playlistMenuItems = remember {
        listOf(
            Pair("New Playlist", "playlist_add"),
            Pair("Sort Playlists", "sort"),
            Pair("Import M3U", "storage")
        )
    }

    var playlists by remember {
        mutableStateOf(
            listOf(
                PlaylistItem(1, "Zen Meditation", 8, "32 mins", isZenCover = true),
                PlaylistItem(2, "Midnight Chillhop", 14, "48 mins"),
                PlaylistItem(3, "Ambient Focus Flow", 10, "42 mins"),
                PlaylistItem(4, "Night Drive Beats", 12, "50 mins"),
                PlaylistItem(5, "Acoustic Mornings", 9, "34 mins"),
                PlaylistItem(6, "Retro Wave Synth", 16, "1 hr 02 min")
            )
        )
    }

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Playlists",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch,
                onOpenSettings = onOpenSettings,
                onOpenEqualizer = onOpenEqualizer
            )
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
                    // "Playlists" Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        // Create New Playlist Card
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clickable { }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(
                                            name = "playlist_add",
                                            size = 22.dp,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Create Playlist",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Tap to create a new custom playlist",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        // Playlists List
                        items(playlists, key = { it.id }) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSongSelected(playlist.name, "Playlist")
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (playlist.isZenCover) {
                                        KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                    } else {
                                        AppIcon(
                                            name = "playlist",
                                            size = 24.dp,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playlist.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${playlist.trackCount} tracks • ${playlist.duration}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    AppIcon(
                                        name = "more_vert",
                                        size = 20.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // "Favorites" Tab
                    if (likedSongs.isEmpty()) {
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
                                        name = "heart_border",
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No Favorites Yet",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Songs marked with a heart will appear here automatically.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                        ) {
                            items(likedSongs) { (title, artist) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSongSelected(title, artist) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = artist,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    AppIcon(name = "heart", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Folders" Tab
                    val folders = remember {
                        listOf(
                            Triple("Music/Ambient", "18 tracks", "1.4 GB"),
                            Triple("Music/Chillhop", "24 tracks", "2.1 GB"),
                            Triple("Music/Zen Echoes", "12 tracks", "940 MB"),
                            Triple("Music/Lossless FLAC", "36 tracks", "4.8 GB")
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        items(folders) { (folderName, count, size) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(name = "storage", size = 20.dp, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = folderName,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "$count • $size",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AppIcon(name = "chevron_right", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
