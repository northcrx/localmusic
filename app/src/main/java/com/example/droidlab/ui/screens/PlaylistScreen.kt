package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow

data class PlaylistItem(
    val id: Int,
    val name: String,
    val trackCount: Int,
    val duration: String,
    val iconName: String = "playlist"
)

/**
 * Playlist Screen matching user's exact specification & Screenshot:
 * - Top card: Liked songs (with heart icon, count, duration, chevron right)
 * - Section header: "Your playlists" on left, "Recently updated ↓" on right
 * - 2-Column Grid with curated & custom playlists (Slow mornings, A quiet corner, Evening walks, On repeat)
 * - Footer: lock icon + "Saved on your device. Ready whenever you are."
 * - Plus button to the left of search button in top bar creates new playlists
 * - Detailed playlist view on tap with full track list and playback
 */
@Composable
fun PlaylistRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    currentSongTitle: String = "",
    isPlaying: Boolean = false,
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var playlists by remember {
        mutableStateOf(
            listOf(
                PlaylistItem(1, "Slow mornings", 12, "46 min", "coffee"),
                PlaylistItem(2, "A quiet corner", 18, "1 hr 12 min", "book"),
                PlaylistItem(3, "Evening walks", 16, "58 min", "moon"),
                PlaylistItem(4, "On repeat", 9, "34 min", "repeat")
            )
        )
    }

    var selectedPlaylistForDetail by remember { mutableStateOf<PlaylistItem?>(null) }
    var isLikedSongsDetailOpen by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Playlists",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch,
                onOpenSettings = onOpenSettings,
                onOpenEqualizer = onOpenEqualizer,
                onAddClick = { showCreatePlaylistDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp)
            ) {
                // 1. Top Card: Liked songs
                item {
                    val totalLikedCount = likedSongs.size.coerceAtLeast(24)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLikedSongsDetailOpen = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left squarish rounded heart container
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(
                                    name = "heart_border",
                                    size = 22.dp,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            // Middle: Title and subtitle
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Liked songs",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = "$totalLikedCount songs · 1 hr 36 min",
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Right: Chevron right
                            AppIcon(
                                name = "chevron_right",
                                size = 20.dp,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // 2. Section Header: "Your playlists" & "Recently updated ↓"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your playlists",
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Recently updated ↓",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. 2-Column Grid of Playlists
                val chunkedPlaylists = playlists.chunked(2)
                items(chunkedPlaylists) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (playlist in pair) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPlaylistForDetail = playlist }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Top Row: Squarish icon container + 3 horizontal dots
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(
                                                name = playlist.iconName,
                                                size = 20.dp,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { selectedPlaylistForDetail = playlist },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            AppIcon(
                                                name = "more_horiz",
                                                size = 18.dp,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(18.dp))

                                    // Playlist Title
                                    Text(
                                        text = playlist.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(Modifier.height(3.dp))

                                    // Songs count
                                    Text(
                                        text = "${playlist.trackCount} songs",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Duration
                                    Text(
                                        text = playlist.duration,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // If single item in row, fill with empty spacer
                        if (pair.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }

                // 4. Footer: lock icon + "Saved on your device. Ready whenever you are."
                item {
                    Spacer(Modifier.height(28.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            name = "lock",
                            size = 14.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Saved on your device. Ready whenever you are.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    // Create New Playlist Dialog
    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreatePlaylistDialog = false
                newPlaylistName = ""
            },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "playlist_add", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("New Playlist", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text("Playlist name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            val newId = (playlists.maxOfOrNull { it.id } ?: 0) + 1
                            playlists = playlists + PlaylistItem(
                                id = newId,
                                name = newPlaylistName.trim(),
                                trackCount = 0,
                                duration = "0 min",
                                iconName = "playlist"
                            )
                        }
                        showCreatePlaylistDialog = false
                        newPlaylistName = ""
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreatePlaylistDialog = false
                        newPlaylistName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Playlist Detail View
    if (selectedPlaylistForDetail != null) {
        val playlist = selectedPlaylistForDetail!!
        val playlistTracks = remember(playlist) {
            listOf(
                Triple("Kyoto Garden", "Zen Echoes", "2:58"),
                Triple("Midnight Drive", "Nova Lane", "4:12"),
                Triple("Blue Bottle Coffee", "Chillhop Beats", "3:45"),
                Triple("The Reading Room", "Ambient Works", "3:30"),
                Triple("Neon Horizon", "Retro Wave", "4:05"),
                Triple("Solar Drift", "Cosmic Sound", "3:50"),
                Triple("Golden Hour", "Acoustic Sunset", "3:15"),
                Triple("Rainy Cafe", "Lo-Fi Study", "2:45")
            ).take(playlist.trackCount.coerceAtLeast(4))
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedPlaylistForDetail = null }) {
                        AppIcon(name = "arrow_back", size = 22.dp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Playlist",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(
                                    name = playlist.iconName,
                                    size = 36.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = playlist.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${playlist.trackCount} songs • ${playlist.duration}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        if (playlistTracks.isNotEmpty()) {
                                            onSongSelected(playlistTracks.first().first, playlistTracks.first().second)
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    AppIcon(name = "play", size = 18.dp, tint = Color.White)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Play All")
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (playlistTracks.isNotEmpty()) {
                                            val picked = playlistTracks.random()
                                            onSongSelected(picked.first, picked.second)
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    AppIcon(name = "shuffle", size = 18.dp)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Shuffle")
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Tracks",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(playlistTracks) { (trackTitle, trackArtist, trackDuration) ->
                        DroidLabTrackRow(
                            title = trackTitle,
                            artist = trackArtist,
                            duration = trackDuration,
                            isPlaying = isPlaying && currentSongTitle == trackTitle,
                            isLiked = likedSongs.any { it.first == trackTitle },
                            onToggleLike = { onToggleLike(trackTitle, trackArtist) },
                            onClick = { onSongSelected(trackTitle, trackArtist) }
                        )
                    }
                }
            }
        }
    }

    // Liked Songs Detail View
    if (isLikedSongsDetailOpen) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isLikedSongsDetailOpen = false }) {
                        AppIcon(name = "arrow_back", size = 22.dp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Liked Songs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (likedSongs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No liked songs yet",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        items(likedSongs) { (trackTitle, trackArtist) ->
                            DroidLabTrackRow(
                                title = trackTitle,
                                artist = trackArtist,
                                duration = "3:30",
                                isPlaying = isPlaying && currentSongTitle == trackTitle,
                                isLiked = true,
                                onToggleLike = { onToggleLike(trackTitle, trackArtist) },
                                onClick = { onSongSelected(trackTitle, trackArtist) }
                            )
                        }
                    }
                }
            }
        }
    }
}
