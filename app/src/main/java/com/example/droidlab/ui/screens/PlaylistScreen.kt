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
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow
import com.example.droidlab.ui.components.MusicTrackArtCover

data class PlaylistItem(
    val id: Int,
    val name: String,
    val trackCount: Int,
    val duration: String,
    val iconName: String = "playlist",
    val isFavorite: Boolean = false,
    val coverSongTitle: String? = null,
    val coverSongArtist: String? = null
)

/**
 * Playlist Screen matching user's exact specification & Screenshots 3 and 4:
 * - Playlists Grid view (Screenshot 4):
 *   - Top card: Liked songs (heart icon, count, duration, chevron right)
 *   - Section header: "Your playlists" & "Recently updated ↓"
 *   - 2-Column Grid: Slow mornings, A quiet corner, Evening walks, On repeat
 *   - Footer: lock icon + "Saved on your device. Ready whenever you are."
 * - Playlist Detail Screen (Screenshot 3):
 *   - Opens when tapping any playlist card (not just playing)
 *   - Back link: "< Playlists"
 *   - Large Title ("Liked songs" or playlist name)
 *   - Subtitle: "${count} songs · ${duration} · On device"
 *   - Top right: Favorite Playlist button (add/remove playlist to favorites!)
 *   - Search Bar: "Search your music" pill
 *   - Action row: "Recently liked ↓" on left, "Shuffle all" on right
 *   - Track list with artwork, title, artist, duration, and heart icon
 *   - "More like your likes" listening suggestions section with 2 rounded cards
 *   - Bottom subtitle: "Listening ideas only · Nothing is added automatically"
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
                PlaylistItem(1, "Slow mornings", 12, "46 min", "coffee", isFavorite = false, coverSongTitle = "Blue Bottle Coffee", coverSongArtist = "Chillhop Beats"),
                PlaylistItem(2, "A quiet corner", 18, "1 hr 12 min", "book", isFavorite = true, coverSongTitle = "The Reading Room", coverSongArtist = "Ambient Works"),
                PlaylistItem(3, "Evening walks", 16, "58 min", "moon", isFavorite = false, coverSongTitle = "Midnight Drive", coverSongArtist = "Nova Lane"),
                PlaylistItem(4, "On repeat", 9, "34 min", "repeat", isFavorite = true, coverSongTitle = "Kyoto Garden", coverSongArtist = "Zen Echoes")
            )
        )
    }

    var selectedPlaylistForDetail by remember { mutableStateOf<PlaylistItem?>(null) }
    var isLikedSongsDetailOpen by remember { mutableStateOf(false) }
    var isLikedSongsFavorite by remember { mutableStateOf(true) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // If a playlist or liked songs is open, render the detail screen (Screenshot 3)
    if (isLikedSongsDetailOpen || selectedPlaylistForDetail != null) {
        val isLikedDetail = isLikedSongsDetailOpen
        val currentPlaylist = selectedPlaylistForDetail

        val playlistName = if (isLikedDetail) "Liked songs" else (currentPlaylist?.name ?: "Playlist")
        val isCurrentFavorite = if (isLikedDetail) isLikedSongsFavorite else (currentPlaylist?.isFavorite ?: false)
        val defaultSongCount = if (isLikedDetail) likedSongs.size.coerceAtLeast(24) else (currentPlaylist?.trackCount ?: 12)
        val defaultDuration = if (isLikedDetail) "1 hr 36 min" else (currentPlaylist?.duration ?: "46 min")

        // Tracks for this playlist
        val baseTracks = remember(isLikedDetail, currentPlaylist, likedSongs) {
            if (isLikedDetail) {
                if (likedSongs.isNotEmpty()) {
                    likedSongs.map { Triple(it.first, it.second, "3:45") }
                } else {
                    listOf(
                        Triple("Kyoto Garden", "Zen Echoes", "4:12"),
                        Triple("Blue Bottle Coffee", "Chillhop Beats", "3:45"),
                        Triple("The Reading Room", "Ambient Works", "5:08"),
                        Triple("Midnight Drive", "Nova Lane", "4:12"),
                        Triple("Neon Horizon", "Retro Wave", "4:05"),
                        Triple("Solar Drift", "Cosmic Sound", "3:50"),
                        Triple("Golden Hour", "Acoustic Sunset", "3:15"),
                        Triple("Rainy Cafe", "Lo-Fi Study", "2:45")
                    )
                }
            } else {
                listOf(
                    Triple("${currentPlaylist?.name ?: "Track"} Part I", "Zen Echoes", "3:45"),
                    Triple("Blue Bottle Coffee", "Chillhop Beats", "3:45"),
                    Triple("The Reading Room", "Ambient Works", "5:08"),
                    Triple("Midnight Drive", "Nova Lane", "4:12"),
                    Triple("Evening Solitude", "Quiet Echoes", "3:30"),
                    Triple("Sunset Reflections", "Nova Lane", "4:18")
                ).take(currentPlaylist?.trackCount?.coerceAtLeast(4) ?: 6)
            }
        }

        // Filtered tracks based on in-playlist search
        val filteredTracks = if (searchQuery.isBlank()) {
            baseTracks
        } else {
            baseTracks.filter {
                it.first.contains(searchQuery, ignoreCase = true) ||
                it.second.contains(searchQuery, ignoreCase = true)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // 1. Top Bar: "< Playlists" on left, Favorite Playlist Heart button on right
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back to playlists button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    isLikedSongsDetailOpen = false
                                    selectedPlaylistForDetail = null
                                    searchQuery = ""
                                }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            AppIcon(
                                name = "chevron_left",
                                size = 20.dp,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Playlists",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Favorite Playlist Button (Screenshot 3)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    if (isLikedDetail) {
                                        isLikedSongsFavorite = !isLikedSongsFavorite
                                    } else if (currentPlaylist != null) {
                                        val updated = currentPlaylist.copy(isFavorite = !currentPlaylist.isFavorite)
                                        selectedPlaylistForDetail = updated
                                        playlists = playlists.map { if (it.id == updated.id) updated else it }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                name = if (isCurrentFavorite) "heart" else "heart_border",
                                size = 20.dp,
                                tint = if (isCurrentFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Large Title and Subtitle (Screenshot 3)
                item {
                    Text(
                        text = playlistName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$defaultSongCount songs · $defaultDuration · On device",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(18.dp))
                }

                // 3. Search Bar Pill: "Search your music"
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search your music",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            AppIcon(
                                name = "search",
                                size = 18.dp,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    AppIcon(name = "close", size = 16.dp)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                    Spacer(Modifier.height(18.dp))
                }

                // 4. Action Row: "Recently liked ↓" on left, "Shuffle all" on right
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLikedDetail) "Recently liked ↓" else "Recently added ↓",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (filteredTracks.isNotEmpty()) {
                                        val picked = filteredTracks.random()
                                        onSongSelected(picked.first, picked.second)
                                    }
                                }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        ) {
                            AppIcon(
                                name = "shuffle",
                                size = 16.dp,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Shuffle all",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 5. Track Entries
                items(filteredTracks) { (title, artist, duration) ->
                    val isThisPlaying = isPlaying && currentSongTitle == title
                    val isLiked = likedSongs.any { it.first == title } || isLikedDetail

                    DroidLabTrackRow(
                        title = title,
                        artist = artist,
                        duration = duration,
                        isPlaying = isThisPlaying,
                        isLiked = isLiked,
                        onToggleLike = { onToggleLike(title, artist) },
                        onClick = { onSongSelected(title, artist) }
                    )
                }

                // 6. Section: "More like your likes" (Screenshot 3)
                item {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = if (isLikedDetail) "More like your likes" else "More like this playlist",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "A few listening ideas from your library, inspired by the artists you've liked.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))

                    // 2 Rounded Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 1: After the Rain
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSongSelected("After the Rain", "Zen Echoes") }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "music",
                                        size = 20.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    AppIcon(
                                        name = "heart_border",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                Spacer(Modifier.height(18.dp))
                                Text(
                                    text = "After the Rain",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Zen Echoes",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Card 2: Paper Lanterns
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSongSelected("Paper Lanterns", "Ambient Works") }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "music",
                                        size = 20.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    AppIcon(
                                        name = "heart_border",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                Spacer(Modifier.height(18.dp))
                                Text(
                                    text = "Paper Lanterns",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Ambient Works",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Listening ideas only · Nothing is added automatically",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    } else {
        // Main Playlists Screen (Screenshot 4)
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
                    // 1. Top Card: Liked songs (Screenshot 4)
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

                    // 3. 2-Column Grid of Playlists (Screenshot 4)
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
                                                    .size(44.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (playlist.coverSongTitle != null) {
                                                    MusicTrackArtCover(
                                                        title = playlist.coverSongTitle,
                                                        artist = playlist.coverSongArtist ?: "",
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                } else {
                                                    AppIcon(
                                                        name = playlist.iconName,
                                                        size = 20.dp,
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (playlist.isFavorite) {
                                                    AppIcon(
                                                        name = "heart",
                                                        size = 14.dp,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(Modifier.width(4.dp))
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
    }

    var selectedIconForNewPlaylist by remember { mutableStateOf("playlist") }
    var selectedCoverForNewPlaylist by remember { mutableStateOf<Pair<String, String>?>(null) }

    val availableIcons = remember {
        listOf("playlist", "coffee", "book", "moon", "repeat", "music", "heart", "headset", "star")
    }

    val availableSongCovers = remember {
        listOf(
            "Auto (1st Song)" to "",
            "Kyoto Garden" to "Zen Echoes",
            "Blue Bottle Coffee" to "Chillhop Beats",
            "Midnight Drive" to "Nova Lane",
            "Neon Horizon" to "Retro Wave",
            "The Reading Room" to "Ambient Works"
        )
    }

    // Create New Playlist Dialog
    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreatePlaylistDialog = false
                newPlaylistName = ""
                selectedCoverForNewPlaylist = null
                selectedIconForNewPlaylist = "playlist"
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

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Choose Icon",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableIcons.take(6).forEach { iconName ->
                            val isSelected = selectedIconForNewPlaylist == iconName && selectedCoverForNewPlaylist == null
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        selectedIconForNewPlaylist = iconName
                                        selectedCoverForNewPlaylist = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(
                                    name = iconName,
                                    size = 18.dp,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Or Choose Cover Artwork",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableSongCovers.take(4).forEach { (coverTitle, coverArtist) ->
                            val isSelected = if (coverTitle == "Auto (1st Song)") {
                                selectedCoverForNewPlaylist == null && selectedIconForNewPlaylist == "playlist"
                            } else {
                                selectedCoverForNewPlaylist?.first == coverTitle
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    )
                                    .clickable {
                                        if (coverTitle == "Auto (1st Song)") {
                                            selectedCoverForNewPlaylist = null
                                            selectedIconForNewPlaylist = "playlist"
                                        } else {
                                            selectedCoverForNewPlaylist = Pair(coverTitle, coverArtist)
                                        }
                                    }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (coverTitle == "Auto (1st Song)") {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AppIcon(name = "music", size = 16.dp, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Auto", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                } else {
                                    MusicTrackArtCover(
                                        title = coverTitle,
                                        artist = coverArtist,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            val newId = (playlists.maxOfOrNull { it.id } ?: 0) + 1
                            val finalCover = selectedCoverForNewPlaylist ?: Pair("Kyoto Garden", "Zen Echoes")
                            playlists = playlists + PlaylistItem(
                                id = newId,
                                name = newPlaylistName.trim(),
                                trackCount = 0,
                                duration = "0 min",
                                iconName = selectedIconForNewPlaylist,
                                isFavorite = false,
                                coverSongTitle = finalCover.first,
                                coverSongArtist = finalCover.second
                            )
                        }
                        showCreatePlaylistDialog = false
                        newPlaylistName = ""
                        selectedCoverForNewPlaylist = null
                        selectedIconForNewPlaylist = "playlist"
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
                        selectedCoverForNewPlaylist = null
                        selectedIconForNewPlaylist = "playlist"
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
