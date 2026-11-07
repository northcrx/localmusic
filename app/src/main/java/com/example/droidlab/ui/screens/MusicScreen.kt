package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.droidlab.ui.components.DroidLabTrackRow

data class AlbumItem(
    val id: Int,
    val title: String,
    val artist: String,
    val trackCount: Int,
    val year: String,
    val initialColor: Color
)

data class ArtistItem(
    val id: Int,
    val name: String,
    val genre: String,
    val listeners: String,
    val initialColor: Color,
    val isFollowing: Boolean
)

/**
 * Music Screen matching user specifications:
 * - Tabs: "For you", "Albums", "Artists"
 * - "For you": Recommended/liked songs on top, all songs below
 * - "Albums": Albums grid with artwork and track counts
 * - "Artists": Artists list with profile avatars and follow status
 */
@Composable
fun MusicRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("For you", "Albums", "Artists")

    val musicMenuItems = remember {
        listOf(
            Pair("Sort Tracks", "tune"),
            Pair("Scan Library", "search"),
            Pair("Shuffle All", "shuffle"),
            Pair("Equalizer", "tune")
        )
    }

    val allTracks = remember {
        listOf(
            Pair("Blue Bottle Coffee", "Chillhop Beats"),
            Pair("Kyoto Garden", "Zen Echoes"),
            Pair("The Reading Room", "Ambient Works"),
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Neon Horizon", "Retro Wave"),
            Pair("Solar Drift", "Cosmic Sound"),
            Pair("Golden Hour", "Acoustic Sunset"),
            Pair("Rainy Cafe", "Lo-Fi Study"),
            Pair("Velvet Moon", "Jazz Quartet")
        )
    }

    val sampleAlbums = remember {
        listOf(
            AlbumItem(1, "Neon Hours", "Retro Wave", 12, "2024", Color(0xFF6366F1)),
            AlbumItem(2, "Zen Echoes", "Zen Echoes", 8, "2023", Color(0xFF10B981)),
            AlbumItem(3, "Chillhop Essentials", "Chillhop Beats", 15, "2024", Color(0xFFF59E0B)),
            AlbumItem(4, "Midnight Sessions", "Nova Lane", 10, "2024", Color(0xFF3B82F6)),
            AlbumItem(5, "Cosmic Horizon", "Cosmic Sound", 9, "2023", Color(0xFFEC4899)),
            AlbumItem(6, "Acoustic Mornings", "Acoustic Sunset", 11, "2024", Color(0xFF8B5CF6))
        )
    }

    var sampleArtists by remember {
        mutableStateOf(
            listOf(
                ArtistItem(1, "Nova Lane", "Synthwave / Cyberpunk", "1.4M monthly", Color(0xFF3B82F6), true),
                ArtistItem(2, "Chillhop Beats", "Lo-Fi Instrumental", "890K monthly", Color(0xFF10B981), true),
                ArtistItem(3, "Zen Echoes", "Ambient Soundscapes", "520K monthly", Color(0xFF8B5CF6), false),
                ArtistItem(4, "Retro Wave", "Electronic & Synth", "2.1M monthly", Color(0xFFF59E0B), true),
                ArtistItem(5, "Cosmic Sound", "Deep Space Ambient", "340K monthly", Color(0xFFEC4899), false),
                ArtistItem(6, "Acoustic Sunset", "Folk & Acoustic Chill", "480K monthly", Color(0xFF06B6D4), false)
            )
        )
    }

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Music",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch,
                menuItems = musicMenuItems
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
                    // "For you" Tab: Recommended/Liked on top, all songs below
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        // Section 1: Recommendations based on liked & listen history
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Recommended For You",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Based on your taste",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val topRecommendations = remember(likedSongs) {
                                    if (likedSongs.isNotEmpty()) {
                                        likedSongs + allTracks.filter { track ->
                                            likedSongs.none { it.first == track.first }
                                        }.take(3)
                                    } else {
                                        allTracks.take(4)
                                    }
                                }

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(topRecommendations) { (title, artist) ->
                                        Card(
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { onSongSelected(title, artist) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        AppIcon(name = "music", size = 18.dp)
                                                    }

                                                    val isThisLiked = likedSongs.any { it.first == title && it.second == artist }
                                                    if (isThisLiked) {
                                                        AppIcon(
                                                            name = "heart",
                                                            size = 18.dp,
                                                            tint = Color(0xFFEF4444)
                                                        )
                                                    }
                                                }

                                                Column {
                                                    Text(
                                                        text = title,
                                                        fontSize = 13.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(Modifier.height(2.dp))
                                                    Text(
                                                        text = artist,
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: All Songs header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "All Songs (${allTracks.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Library",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // All Tracks list
                        itemsIndexed(allTracks) { index, (title, artist) ->
                            DroidLabTrackRow(
                                index = index + 1,
                                title = title,
                                artist = artist,
                                duration = when (index % 4) {
                                    0 -> "3:45"
                                    1 -> "4:12"
                                    2 -> "2:58"
                                    else -> "3:30"
                                },
                                onClick = {
                                    onSongSelected(title, artist)
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // "Albums" Tab: Grid of albums
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sampleAlbums, key = { it.id }) { album ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSongSelected(album.title, album.artist) }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    // Album Cover Placeholder
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(album.initialColor.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(album.initialColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(name = "library_music", size = 22.dp, tint = Color.White)
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    Text(
                                        text = album.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(Modifier.height(2.dp))

                                    Text(
                                        text = "${album.artist} • ${album.trackCount} tracks",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Artists" Tab: List of artists with profile avatar & Follow/Following
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Artists in your library",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(sampleArtists, key = { it.id }) { artist ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(artist.initialColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = artist.name.take(1).uppercase(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artist.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${artist.genre} • ${artist.listeners}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            sampleArtists = sampleArtists.map {
                                                if (it.id == artist.id) it.copy(isFollowing = !it.isFollowing) else it
                                            }
                                        },
                                        colors = if (artist.isFollowing) {
                                            ButtonDefaults.filledTonalButtonColors()
                                        } else {
                                            ButtonDefaults.buttonColors()
                                        },
                                        shape = RoundedCornerShape(99.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (artist.isFollowing) "Following" else "Follow",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
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
