package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.droidlab.ui.components.MusicTrackArtCover

data class AlbumItem(
    val id: Int,
    val title: String,
    val artist: String,
    val trackCount: Int,
    val year: String = "2024"
)

data class ArtistItem(
    val id: Int,
    val name: String,
    val albumsCount: Int,
    val songsCount: Int
)

/**
 * Album Detail Screen: Reusable full-screen destination
 */
@Composable
fun AlbumDetailScreen(
    album: AlbumItem,
    onBack: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit,
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> },
    currentSongTitle: String = "",
    isPlaying: Boolean = false,
    onAlbumSelected: ((AlbumItem) -> Unit)? = null
) {
    var albumSearchQuery by remember { mutableStateOf("") }
    var isAlbumFavorite by remember { mutableStateOf(false) }

    val albumTracks = remember(album) {
        listOf(
            Pair("${album.title} (Intro)", "2:45"),
            Pair("${album.title} Part I", "3:30"),
            Pair("${album.title} Part II", "4:12"),
            Pair("Echoes & Whispers", "3:18"),
            Pair("Quiet Morning", "2:58"),
            Pair("Drifting Harmony", "3:40"),
            Pair("Solitude", "4:05"),
            Pair("Closing Cadence", "3:15")
        )
    }

    val filteredAlbumTracks = if (albumSearchQuery.isBlank()) {
        albumTracks
    } else {
        albumTracks.filter {
            it.first.contains(albumSearchQuery, ignoreCase = true) ||
            album.artist.contains(albumSearchQuery, ignoreCase = true)
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
            // 1. Top Bar: "< Back" on left, Favorite Album Heart on right
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBack() }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        AppIcon(
                            name = "chevron_left",
                            size = 20.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Favorite Album Heart Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { isAlbumFavorite = !isAlbumFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            name = if (isAlbumFavorite) "heart" else "heart_border",
                            size = 20.dp,
                            tint = if (isAlbumFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Large Title, Artwork, and Subtitle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        MusicTrackArtCover(
                            title = album.title,
                            artist = album.artist,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = album.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${album.artist} · ${album.trackCount} tracks · ${album.year} · On device",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // 3. Search Bar Pill: "Search in this album"
            item {
                OutlinedTextField(
                    value = albumSearchQuery,
                    onValueChange = { albumSearchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search in ${album.title}",
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
                        if (albumSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { albumSearchQuery = "" }) {
                                AppIcon(name = "close", size = 16.dp)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. Action Row: Track count and Shuffle All
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredAlbumTracks.size} tracks",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (filteredAlbumTracks.isNotEmpty()) {
                                    val randomTrack = filteredAlbumTracks.random()
                                    onSongSelected(randomTrack.first, album.artist)
                                }
                            }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        AppIcon(
                            name = "shuffle",
                            size = 17.dp,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Shuffle",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // 5. Track items list
            items(filteredAlbumTracks) { (title, duration) ->
                var showDropdown by remember { mutableStateOf(false) }
                val isTrackLiked = likedSongs.any { it.first == title }
                val isCurrentlyPlayingThis = isPlaying && currentSongTitle == title

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongSelected(title, album.artist) }
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        MusicTrackArtCover(
                            title = title,
                            artist = album.artist,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCurrentlyPlayingThis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = album.artist,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = duration,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(4.dp))

                    Box {
                        IconButton(
                            onClick = { showDropdown = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (isCurrentlyPlayingThis) {
                                AppIcon(name = "graphic_eq", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                            } else {
                                AppIcon(name = "more_vert", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Play next") },
                                leadingIcon = { AppIcon(name = "queue", size = 18.dp) },
                                onClick = {
                                    onSongSelected(title, album.artist)
                                    showDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isTrackLiked) "Unlike" else "Add to Liked") },
                                leadingIcon = {
                                    AppIcon(
                                        name = if (isTrackLiked) "heart" else "heart_border",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    onToggleLike(title, album.artist)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // 6. Listening ideas footer
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "More from ${album.artist}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSongSelected("Sunset Glow", album.artist) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(14.dp))
                            Text(text = "Sunset Glow", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Spacer(Modifier.height(2.dp))
                            Text(text = album.artist, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSongSelected("Solitude", album.artist) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(14.dp))
                            Text(text = "Solitude", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Spacer(Modifier.height(2.dp))
                            Text(text = album.artist, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
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
}

/**
 * Artist Detail Screen: Reusable full-screen destination
 */
@Composable
fun ArtistDetailScreen(
    artist: ArtistItem,
    onBack: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit,
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> },
    currentSongTitle: String = "",
    isPlaying: Boolean = false,
    onAlbumSelected: ((AlbumItem) -> Unit)? = null
) {
    var artistSearchQuery by remember { mutableStateOf("") }
    var isArtistFavorite by remember { mutableStateOf(false) }

    val artistTracks = remember(artist) {
        listOf(
            Pair("${artist.name} - Signature Theme", "3:45"),
            Pair("${artist.name} - Evening Echo", "4:10"),
            Pair("${artist.name} - Midnight Breeze", "3:25"),
            Pair("${artist.name} - Skyline Melody", "3:58"),
            Pair("${artist.name} - Ambient Waves", "4:20"),
            Pair("${artist.name} - Quiet Morning", "3:10")
        )
    }

    val artistAlbums = remember(artist) {
        listOf(
            AlbumItem(101, "${artist.name} Vol. 1", artist.name, 8, "2023"),
            AlbumItem(102, "${artist.name} Essentials", artist.name, 12, "2024")
        )
    }

    val filteredArtistTracks = if (artistSearchQuery.isBlank()) {
        artistTracks
    } else {
        artistTracks.filter {
            it.first.contains(artistSearchQuery, ignoreCase = true) ||
            artist.name.contains(artistSearchQuery, ignoreCase = true)
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
            // 1. Top Bar: "< Back" on left, Favorite Heart on right
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBack() }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        AppIcon(
                            name = "chevron_left",
                            size = 20.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Favorite Artist Heart Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { isArtistFavorite = !isArtistFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            name = if (isArtistFavorite) "heart" else "heart_border",
                            size = 20.dp,
                            tint = if (isArtistFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Large Title, Artist Avatar, and Subtitle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = artist.name.take(1).uppercase(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = artist.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${artist.albumsCount} albums · ${artist.songsCount} songs · On device",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // 3. Search Bar Pill: "Search in artist songs"
            item {
                OutlinedTextField(
                    value = artistSearchQuery,
                    onValueChange = { artistSearchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search in ${artist.name}",
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
                        if (artistSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { artistSearchQuery = "" }) {
                                AppIcon(name = "close", size = 16.dp)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. Albums Section
            item {
                Text(
                    text = "Albums",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(artistAlbums) { album ->
                        Column(
                            modifier = Modifier
                                .width(110.dp)
                                .clickable {
                                    if (onAlbumSelected != null) {
                                        onAlbumSelected(album)
                                    } else {
                                        onSongSelected("${album.title} (Intro)", artist.name)
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                MusicTrackArtCover(
                                    title = album.title,
                                    artist = artist.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = album.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${album.trackCount} tracks",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // 5. Popular Songs Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Songs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (filteredArtistTracks.isNotEmpty()) {
                                    val randomTrack = filteredArtistTracks.random()
                                    onSongSelected(randomTrack.first, artist.name)
                                }
                            }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        AppIcon(
                            name = "shuffle",
                            size = 17.dp,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Shuffle",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // 6. Tracks list
            items(filteredArtistTracks) { (title, duration) ->
                var showDropdown by remember { mutableStateOf(false) }
                val isTrackLiked = likedSongs.any { it.first == title }
                val isCurrentlyPlayingThis = isPlaying && currentSongTitle == title

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongSelected(title, artist.name) }
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        MusicTrackArtCover(
                            title = title,
                            artist = artist.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCurrentlyPlayingThis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = artist.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = duration,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(4.dp))

                    Box {
                        IconButton(
                            onClick = { showDropdown = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (isCurrentlyPlayingThis) {
                                AppIcon(name = "graphic_eq", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                            } else {
                                AppIcon(name = "more_vert", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Play next") },
                                leadingIcon = { AppIcon(name = "queue", size = 18.dp) },
                                onClick = {
                                    onSongSelected(title, artist.name)
                                    showDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isTrackLiked) "Unlike" else "Add to Liked") },
                                leadingIcon = {
                                    AppIcon(
                                        name = if (isTrackLiked) "heart" else "heart_border",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    onToggleLike(title, artist.name)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Saved locally on your device · Ready to play offline",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}
