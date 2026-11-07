package com.example.droidlab.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.MusicTrackArtCover
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow
import kotlin.math.cos
import kotlin.math.sin

data class TopTrack(
    val rank: Int,
    val title: String,
    val artist: String,
    val playCount: String,
    val duration: String
)

data class HomeAlbumItem(
    val id: Int,
    val title: String,
    val artist: String,
    val trackCount: Int
)

/**
 * Expressive Abstract Art Pattern:
 * 1. Top organic rounded pebble/triangle
 * 2. Bottom-left multi-pointed star / flower
 * 3. Bottom-right squircle / rounded square
 */
@Composable
fun HomeAbstractArtPattern(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Top rounded pebble / teardrop shape
        val pebblePath = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            cubicTo(w * 0.65f, h * 0.10f, w * 0.72f, h * 0.38f, w * 0.62f, h * 0.44f)
            cubicTo(w * 0.55f, h * 0.48f, w * 0.45f, h * 0.48f, w * 0.38f, h * 0.44f)
            cubicTo(w * 0.28f, h * 0.38f, w * 0.35f, h * 0.10f, w * 0.50f, h * 0.10f)
            close()
        }
        drawPath(pebblePath, color = tint)

        // 2. Bottom-Left multi-pointed star / flower shape
        val starCenter = Offset(w * 0.32f, h * 0.68f)
        val outerR = minOf(w, h) * 0.18f
        val innerR = outerR * 0.72f
        val points = 8
        val starPath = Path().apply {
            for (i in 0 until points * 2) {
                val r = if (i % 2 == 0) outerR else innerR
                val angle = (i * Math.PI / points) - Math.PI / 2
                val x = starCenter.x + (r * cos(angle)).toFloat()
                val y = starCenter.y + (r * sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(starPath, color = tint)

        // 3. Bottom-Right squircle / rounded rectangle
        val sqSize = minOf(w, h) * 0.32f
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.54f, h * 0.52f),
            size = Size(sqSize, sqSize),
            cornerRadius = CornerRadius(sqSize * 0.28f, sqSize * 0.28f)
        )
    }
}

/**
 * Home Screen matching exact Droid Lab specification with:
 * - "For you":
 *     1. 2x2 rounded shuffle cards
 *     2. "Recommended for you" Hero carousel cards
 *     3. "Featured Albums" Square cards + Label
 *     4. "All Songs" Portrait cards
 * - "Liked": where liked songs go with heart unlike action
 * - "Top Tracks": ranked tracks with play counts
 */
@Composable
fun HomeRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> },
    currentSongTitle: String = "",
    isPlaying: Boolean = false
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("For you", "Liked", "Top Tracks")

    val homeMenuItems = remember {
        listOf(
            Pair("Scan Library", "search"),
            Pair("Sort Tracks", "tune"),
            Pair("Recently Played", "music"),
            Pair("Refresh", "refresh")
        )
    }

    val recommendedTracks = remember {
        listOf(
            Pair("Kyoto Garden", "Zen Echoes"),
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Blue Bottle Coffee", "Chillhop Beats"),
            Pair("The Reading Room", "Ambient Works"),
            Pair("Neon Horizon", "Retro Wave"),
            Pair("Solar Drift", "Cosmic Sound"),
            Pair("Golden Hour", "Acoustic Sunset")
        )
    }

    val featuredAlbums = remember {
        listOf(
            HomeAlbumItem(1, "Neon Hours", "Retro Wave", 12),
            HomeAlbumItem(2, "Zen Echoes", "Zen Echoes", 8),
            HomeAlbumItem(3, "Chillhop Essentials", "Chillhop Beats", 15),
            HomeAlbumItem(4, "Midnight Sessions", "Nova Lane", 10),
            HomeAlbumItem(5, "Cosmic Horizon", "Cosmic Sound", 9),
            HomeAlbumItem(6, "Acoustic Mornings", "Acoustic Sunset", 11)
        )
    }

    val topTracks = remember {
        listOf(
            TopTrack(1, "Kyoto Garden", "Zen Echoes", "248 plays", "2:58"),
            TopTrack(2, "Midnight Drive", "Nova Lane", "186 plays", "4:12"),
            TopTrack(3, "Blue Bottle Coffee", "Chillhop Beats", "142 plays", "3:45"),
            TopTrack(4, "Neon Horizon", "Retro Wave", "98 plays", "4:05"),
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
                    // "For you" Tab: 2x2 Shuffle Grid + 3 Horizontal Browse Sections
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        // 1. 2x2 Rounded Shuffle Grid
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Row 1
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Box 1: Shuffle Button Box (Top-Left)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(106.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                val picked = recommendedTracks.random()
                                                onSongSelected(picked.first, picked.second)
                                            },
                                        shape = RoundedCornerShape(16.dp),
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
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(
                                                    name = "shuffle",
                                                    size = 16.dp,
                                                    tint = Color.White
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Shuffle Play",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(3.dp))
                                                Text(
                                                    text = "Tap to mix & play",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    // Box 2 (Top-Right)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(106.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                onSongSelected("Midnight Drive", "Nova Lane")
                                            },
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
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 16.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "Midnight Drive",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(3.dp))
                                                Text(
                                                    text = "Nova Lane",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }

                                // Row 2
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Box 3 (Bottom-Left)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(106.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                onSongSelected("Kyoto Garden", "Zen Echoes")
                                            },
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
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 16.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "Kyoto Garden",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(3.dp))
                                                Text(
                                                    text = "Zen Echoes",
                                                    fontSize = 11.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    // Box 4 (Bottom-Right)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(106.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                onSongSelected("The Reading Room", "Ambient Works")
                                            },
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
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(name = "music", size = 16.dp)
                                            }
                                            Column {
                                                Text(
                                                    text = "The Reading Room",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(3.dp))
                                                Text(
                                                    text = "Ambient Works",
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

                        // Section 1: Hero Carousel with Art + Artist/Title + Play Button
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recommended for you",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                AppIcon(
                                    name = "arrow_forward",
                                    size = 20.dp,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(recommendedTracks.take(4)) { (title, artist) ->
                                    Column(
                                        modifier = Modifier
                                            .width(280.dp)
                                            .clickable { onSongSelected(title, artist) }
                                    ) {
                                        // Large rounded card with music artwork cover
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(22.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            MusicTrackArtCover(
                                                title = title,
                                                artist = artist,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        // Bottom Row: Artist & Title on left, circular Play Button on right
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = artist,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    text = title,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                                    .clickable { onSongSelected(title, artist) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppIcon(
                                                    name = "play",
                                                    size = 20.dp,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: Square album cards with Label underneath
                        item {
                            Spacer(Modifier.height(20.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Featured Albums",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                AppIcon(
                                    name = "arrow_forward",
                                    size = 20.dp,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(featuredAlbums) { album ->
                                    Column(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .clickable { onSongSelected(album.title, album.artist) }
                                    ) {
                                        // Square card with music artwork cover
                                        Box(
                                            modifier = Modifier
                                                .size(96.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            MusicTrackArtCover(
                                                title = album.title,
                                                artist = album.artist,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        // Label below the square
                                        Text(
                                            text = album.title,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // Section 3: Portrait cards with Art on top and Artist + Song at bottom
                        item {
                            Spacer(Modifier.height(20.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "All Songs",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.width(8.dp))
                                AppIcon(
                                    name = "arrow_forward",
                                    size = 20.dp,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(recommendedTracks) { (title, artist) ->
                                    Card(
                                        modifier = Modifier
                                            .width(132.dp)
                                            .height(184.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable { onSongSelected(title, artist) },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Top artwork
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                MusicTrackArtCover(
                                                    title = title,
                                                    artist = artist,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            // Bottom text: Artist & Song
                                            Column {
                                                Text(
                                                    text = artist,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    text = title,
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
                }
                1 -> {
                    // "Liked" Tab: Where the music that you like goes
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
                                    text = "No Liked Songs Yet",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Songs you like will appear here. Tap the heart icon on the music player to save songs.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 96.dp)
                        ) {
                            itemsIndexed(likedSongs) { index, (title, artist) ->
                                DroidLabTrackRow(
                                    index = index + 1,
                                    title = title,
                                    artist = artist,
                                    isLiked = true,
                                    onToggleLike = { onToggleLike(title, artist) },
                                    onClick = { onSongSelected(title, artist) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // "Top Tracks" Tab: Ranked top tracks
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Most Played",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "All Time",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(topTracks, key = { it.rank }) { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSongSelected(track.title, track.artist) }
                                    .padding(horizontal = 16.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (track.rank <= 3) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${track.rank}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (track.rank <= 3) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
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
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${track.artist} • ${track.playCount}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
