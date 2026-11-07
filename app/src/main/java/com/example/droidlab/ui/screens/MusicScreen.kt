package com.example.droidlab.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import kotlin.math.cos
import kotlin.math.sin

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
 * Expressive Abstract Art Pattern reproducing the design from user's screenshot:
 * 1. Top organic rounded pebble/triangle
 * 2. Bottom-left multi-pointed star / flower
 * 3. Bottom-right squircle / rounded square
 */
@Composable
fun AbstractArtPattern(
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
 * Music Screen matching user specifications:
 * - Tabs: "For you", "Albums", "Artists"
 * - "For you":
 *     1. Section title → (Large horizontal carousel card with Art, Artist, Title, and play button)
 *     2. Section title → (Horizontal row of square cards with Label underneath)
 *     3. Section title → (Horizontal row of portrait cards with Art, Artist, and Song)
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
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Kyoto Garden", "Zen Echoes"),
            Pair("The Reading Room", "Ambient Works"),
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
                    // "For you" Tab: Layout matching user specification & screenshot
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        // Section 1: Hero Carousel with Art + Artist/Title + Play Button
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val (title, artist) = allTracks.first()
                                        onSongSelected(title, artist)
                                    }
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
                                items(allTracks.take(4)) { (title, artist) ->
                                    Column(
                                        modifier = Modifier
                                            .width(280.dp)
                                            .clickable { onSongSelected(title, artist) }
                                    ) {
                                        // Large rounded card with abstract artwork pattern
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(22.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AbstractArtPattern(
                                                modifier = Modifier.fillMaxSize(0.85f),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
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
                            Spacer(Modifier.height(18.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTabIndex = 1 }
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
                                items(sampleAlbums) { album ->
                                    Column(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .clickable { onSongSelected(album.title, album.artist) }
                                    ) {
                                        // Square card with abstract pattern
                                        Box(
                                            modifier = Modifier
                                                .size(96.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AbstractArtPattern(
                                                modifier = Modifier.fillMaxSize(0.85f),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
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
                            Spacer(Modifier.height(18.dp))

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
                                items(allTracks) { (title, artist) ->
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
                                                    .height(100.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AbstractArtPattern(
                                                    modifier = Modifier.fillMaxSize(0.9f),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
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
