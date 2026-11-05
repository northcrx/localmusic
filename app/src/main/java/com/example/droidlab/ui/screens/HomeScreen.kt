package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow

data class FollowArtist(
    val id: Int,
    val name: String,
    val genre: String,
    val followers: String,
    val initialColor: Color,
    val isFollowing: Boolean
)

data class TopTrack(
    val rank: Int,
    val title: String,
    val artist: String,
    val playCount: String,
    val duration: String
)

/**
 * Home Screen matching exact Droid Lab specification with:
 * - "For you": 2x2 rounded shuffle cards + tracks
 * - "Following": sample artists with profile icons and Follow / Following button
 * - "Top Tracks": ranked tracks with play counts
 */
@Composable
fun HomeRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("For you", "Following", "Top Tracks")

    val homeMenuItems = remember {
        listOf(
            Pair("Scan Library", "search"),
            Pair("Sort Tracks", "tune"),
            Pair("Recently Played", "music"),
            Pair("Refresh", "refresh")
        )
    }

    val initialTracks = remember {
        listOf(
            Pair("Blue Bottle Coffee", "Chillhop Beats"),
            Pair("Kyoto Garden", "Zen Echoes"),
            Pair("The Reading Room", "Ambient Works"),
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Neon Horizon", "Retro Wave"),
            Pair("Solar Drift", "Cosmic Sound"),
            Pair("Golden Hour", "Acoustic Sunset")
        )
    }

    var trackList by remember { mutableStateOf(initialTracks) }

    // Sample Following Artists
    var artistsList by remember {
        mutableStateOf(
            listOf(
                FollowArtist(1, "Nova Lane", "Synthwave / Cyberpunk", "1.4M followers", Color(0xFF3B82F6), true),
                FollowArtist(2, "Chillhop Beats", "Lo-Fi Instrumental", "890K followers", Color(0xFF10B981), true),
                FollowArtist(3, "Zen Echoes", "Ambient Soundscapes", "520K followers", Color(0xFF8B5CF6), false),
                FollowArtist(4, "Retro Wave", "Electronic & Synth", "2.1M followers", Color(0xFFF59E0B), true),
                FollowArtist(5, "Cosmic Sound", "Deep Space Ambient", "340K followers", Color(0xFFEC4899), false),
                FollowArtist(6, "Acoustic Sunset", "Folk & Acoustic Chill", "480K followers", Color(0xFF06B6D4), false)
            )
        )
    }

    // Top Tracks
    val topTracks = remember {
        listOf(
            TopTrack(1, "Midnight Drive", "Nova Lane", "248 plays", "4:12"),
            TopTrack(2, "Blue Bottle Coffee", "Chillhop Beats", "186 plays", "3:45"),
            TopTrack(3, "Neon Horizon", "Retro Wave", "142 plays", "4:05"),
            TopTrack(4, "Kyoto Garden", "Zen Echoes", "98 plays", "2:58"),
            TopTrack(5, "The Reading Room", "Ambient Works", "76 plays", "3:30"),
            TopTrack(6, "Solar Drift", "Cosmic Sound", "54 plays", "3:50"),
            TopTrack(7, "Golden Hour", "Acoustic Sunset", "43 plays", "3:15")
        )
    }

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Home",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch,
                menuItems = homeMenuItems
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
                    // "For you" Tab: 2x2 Shuffle Grid ONLY HERE + Tracks list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // 4-Box Rounded Shuffle Grid
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Row 1
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Box 1: Shuffle Button Box (Top-Left)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(96.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable {
                                                trackList = trackList.shuffled()
                                                val picked = trackList.first()
                                                onSongSelected(picked.first, picked.second)
                                            },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(
                                                    name = "shuffle",
                                                    size = 18.dp,
                                                    tint = Color.White
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Shuffle Play",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                Text(
                                                    text = "Tap to mix & play",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }

                                    // Box 2 (Top-Right)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(96.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable {
                                                onSongSelected("Midnight Drive", "Nova Lane")
                                            },
                                        shape = RoundedCornerShape(18.dp),
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
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 18.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "Midnight Drive",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Nova Lane",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }

                                // Row 2
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Box 3 (Bottom-Left)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(96.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable {
                                                onSongSelected("Kyoto Garden", "Zen Echoes")
                                            },
                                        shape = RoundedCornerShape(18.dp),
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
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 18.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "Kyoto Garden",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Zen Echoes",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }

                                    // Box 4 (Bottom-Right)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(96.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable {
                                                onSongSelected("The Reading Room", "Ambient Works")
                                            },
                                        shape = RoundedCornerShape(18.dp),
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
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 18.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "The Reading Room",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Ambient Works",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Tracks list
                        itemsIndexed(trackList) { index, (title, artist) ->
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
                    // "Following" Tab: List of artists with profile avatar & Follow/Following toggle
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        item {
                            Text(
                                text = "Artists you follow",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        items(artistsList, key = { it.id }) { artist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Profile User Icon Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(artist.initialColor, artist.initialColor.copy(alpha = 0.6f))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(name = "person", size = 22.dp, tint = Color.White)
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
                                            text = "${artist.genre} • ${artist.followers}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Follow / Following Button
                                    Button(
                                        onClick = {
                                            artistsList = artistsList.map {
                                                if (it.id == artist.id) it.copy(isFollowing = !it.isFollowing) else it
                                            }
                                        },
                                        shape = RoundedCornerShape(99.dp),
                                        colors = if (artist.isFollowing) {
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        } else {
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color.White
                                            )
                                        },
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        if (artist.isFollowing) {
                                            AppIcon(name = "check", size = 15.dp, tint = MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Following", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        } else {
                                            AppIcon(name = "person_add", size = 15.dp, tint = Color.White)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Follow", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // "Top Tracks" Tab: Ranked top tracks with play counts
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        item {
                            Text(
                                text = "Most listened tracks this month",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                        }

                        items(topTracks) { track ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSongSelected(track.title, track.artist)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rank Badge
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (track.rank) {
                                                    1 -> Color(0xFFF59E0B) // Gold
                                                    2 -> Color(0xFF94A3B8) // Silver
                                                    3 -> Color(0xFFB45309) // Bronze
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${track.rank}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (track.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${track.artist} • ${track.playCount}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Text(
                                        text = track.duration,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
