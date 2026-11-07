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
import com.example.droidlab.ui.components.MusicTrackArtCover
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow
import com.example.droidlab.ui.components.KyotoGardenArt

data class TrackItem(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String
)

data class AlbumItem(
    val id: Int,
    val title: String,
    val artist: String,
    val trackCount: Int,
    val year: String
)

data class ArtistItem(
    val id: Int,
    val name: String,
    val albumsCount: Int,
    val songsCount: Int
)

/**
 * Music Screen matching user's exact specification & screenshots:
 * - Tabs: "For you", "Albums", "Artists"
 *
 * 1. "For you":
 *    - Header: "Recently added ⌄" on left, "Shuffle all" on right
 *    - Flat track list with NO 1, 2, 3 rank numbers
 *    - 46dp album artwork thumbnail (KyotoGardenArt for Kyoto Garden, elegant neutral art for others)
 *    - Title & Artist
 *    - Duration & 3 vertical dots (more_vert)
 *    - When playing, 3 dots dynamically change to music sync rhythm bars (graphic_eq)
 *    - Tapping plays in the minibar without force-opening the full player
 *
 * 2. "Albums":
 *    - Clean neutral aesthetic (no multi-color backgrounds or exaggerated corners)
 *    - 4dp minimal rounded square album covers
 *    - Album title, Artist name, and track count (e.g. "12 tracks")
 *
 * 3. "Artists":
 *    - Header: "6 artists" on left, "Artist name A–Z ↓" on right
 *    - Section headers (A, C, N, R, Z)
 *    - Flat rows (no card wrappers): 48dp circular avatar, Name, "${albums} albums • ${songs} songs", chevron_right
 *    - NO Follow button
 *    - Right edge alphabetical A-Z index rail
 */
@Composable
fun MusicRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    currentSongTitle: String = "",
    isPlaying: Boolean = false,
    likedSongs: List<Pair<String, String>> = emptyList(),
    onToggleLike: (title: String, artist: String) -> Unit = { _, _ -> },
    selectedMiniPlayerStyle: String = "Classic Pill",
    onMiniPlayerStyleChanged: (String) -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedAlbumForDetail by remember { mutableStateOf<AlbumItem?>(null) }
    var selectedArtistForDetail by remember { mutableStateOf<ArtistItem?>(null) }
    var albumSearchQuery by remember { mutableStateOf("") }
    var isAlbumFavorite by remember { mutableStateOf(false) }
    var artistSearchQuery by remember { mutableStateOf("") }
    var isArtistFavorite by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val tabs = listOf("For you", "Albums", "Artists", "Mini player")

    val musicMenuItems = remember {
        listOf(
            Pair("Sort Tracks", "tune"),
            Pair("Scan Library", "search"),
            Pair("Shuffle All", "shuffle"),
            Pair("Equalizer", "tune")
        )
    }

    val initialTracks = remember {
        listOf(
            TrackItem(1, "Kyoto Garden", "Zen Echoes", "2:58"),
            TrackItem(2, "Blue Bottle Coffee", "Chillhop Beats", "3:45"),
            TrackItem(3, "Midnight Drive", "Nova Lane", "4:12"),
            TrackItem(4, "The Reading Room", "Ambient Works", "3:30"),
            TrackItem(5, "Neon Horizon", "Retro Wave", "4:05"),
            TrackItem(6, "Solar Drift", "Cosmic Sound", "3:50"),
            TrackItem(7, "Golden Hour", "Acoustic Sunset", "3:15"),
            TrackItem(8, "Rainy Cafe", "Lo-Fi Study", "2:45"),
            TrackItem(9, "Velvet Moon", "Jazz Quartet", "4:32")
        )
    }
    var displayedTracks by remember { mutableStateOf(initialTracks) }
    var currentSortOption by remember { mutableStateOf("Recently added") }

    val albums = remember {
        listOf(
            AlbumItem(1, "Zen Echoes", "Zen Echoes", 8, "2023"),
            AlbumItem(2, "Neon Hours", "Retro Wave", 12, "2024"),
            AlbumItem(3, "Chillhop Essentials", "Chillhop Beats", 15, "2024"),
            AlbumItem(4, "Midnight Sessions", "Nova Lane", 10, "2024"),
            AlbumItem(5, "Cosmic Horizon", "Cosmic Sound", 9, "2023"),
            AlbumItem(6, "Acoustic Mornings", "Acoustic Sunset", 11, "2024")
        )
    }

    val artists = remember {
        listOf(
            ArtistItem(1, "Acoustic Sunset", 2, 11),
            ArtistItem(2, "Ambient Works", 1, 8),
            ArtistItem(3, "Chillhop Beats", 3, 24),
            ArtistItem(4, "Nova Lane", 2, 16),
            ArtistItem(5, "Retro Wave", 4, 32),
            ArtistItem(6, "Zen Echoes", 2, 14)
        )
    }

    val alphabetRail = remember {
        listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Music",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch,
                onOpenSettings = onOpenSettings,
                onOpenEqualizer = onOpenEqualizer,
                onAddClick = { showAddDialog = true }
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
                    // "For you" Tab matching Screenshot 2: Clean flat track list with rhythm bars icon when playing
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
                    ) {
                        // Sub-header Row: "Recently added ⌄" on left, "Shuffle all" on right
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            when (currentSortOption) {
                                                "Recently added" -> {
                                                    currentSortOption = "Title (A–Z)"
                                                    displayedTracks = displayedTracks.sortedBy { it.title }
                                                }
                                                "Title (A–Z)" -> {
                                                    currentSortOption = "Artist (A–Z)"
                                                    displayedTracks = displayedTracks.sortedBy { it.artist }
                                                }
                                                else -> {
                                                    currentSortOption = "Recently added"
                                                    displayedTracks = initialTracks
                                                }
                                            }
                                        }
                                        .padding(vertical = 4.dp, horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = currentSortOption,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    AppIcon(
                                        name = "chevron_down",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            val shuffled = displayedTracks.shuffled()
                                            displayedTracks = shuffled
                                            if (shuffled.isNotEmpty()) {
                                                onSongSelected(shuffled.first().title, shuffled.first().artist)
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
                                        text = "Shuffle all",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Track List
                        items(displayedTracks, key = { it.id }) { track ->
                            val isCurrentSong = currentSongTitle.equals(track.title, ignoreCase = true)
                            val isThisPlaying = isCurrentSong && isPlaying

                            var showDropdown by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSongSelected(track.title, track.artist)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 46dp Square Album Thumbnail (RoundedCornerShape(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (track.title == "Kyoto Garden") {
                                        KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                    } else {
                                        AppIcon(
                                            name = "music",
                                            size = 22.dp,
                                            tint = if (isThisPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                        )
                                    }
                                }

                                Spacer(Modifier.width(14.dp))

                                // Track Title & Artist
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isThisPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = track.artist,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Duration
                                Text(
                                    text = track.duration,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.width(6.dp))

                                // 3-Dots icon or Rhythm waveform bars icon if playing
                                Box {
                                    IconButton(
                                        onClick = {
                                            if (isThisPlaying) {
                                                // If already playing, tapping rhythm icon toggles play/pause or menu
                                                showDropdown = true
                                            } else {
                                                showDropdown = true
                                            }
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        if (isThisPlaying) {
                                            // Music sync rhythm waveform bars icon
                                            AppIcon(
                                                name = "graphic_eq",
                                                size = 20.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            // 3 vertical dots
                                            AppIcon(
                                                name = "more_vert",
                                                size = 20.dp,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
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
                                                onSongSelected(track.title, track.artist)
                                                showDropdown = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(if (likedSongs.any { it.first == track.title }) "Unlike" else "Add to Liked") },
                                            leadingIcon = {
                                                AppIcon(
                                                    name = if (likedSongs.any { it.first == track.title }) "heart" else "heart_border",
                                                    size = 18.dp,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            },
                                            onClick = {
                                                onToggleLike(track.title, track.artist)
                                                showDropdown = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Go to album") },
                                            leadingIcon = { AppIcon(name = "album", size = 18.dp) },
                                            onClick = {
                                                selectedTabIndex = 1
                                                showDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // "Albums" Tab: Clean neutral design, 4dp rounded corners, no multi-color backgrounds
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(albums, key = { it.id }) { album ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedAlbumForDetail = album }
                            ) {
                                // Square album cover (minimal 4dp rounded corners, neutral colors)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (album.title == "Zen Echoes") {
                                        KyotoGardenArt(modifier = Modifier.fillMaxSize())
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(
                                                name = "album",
                                                size = 26.dp,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // Album Name
                                Text(
                                    text = album.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(Modifier.height(2.dp))

                                // Artist Name & Track count (e.g. "Zen Echoes • 8 tracks")
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
                2 -> {
                    // "Artists" Tab matching Screenshot 3:
                    // Header: "6 artists" on left, "Artist name A–Z ↓" on right
                    // Letter headers, 48dp circular avatars, `${albums} albums • ${songs} songs`, chevron_right, A-Z rail
                    Row(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                        ) {
                            // Sub-header Row
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${artists.size} artists",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Artist name A–Z",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        AppIcon(
                                            name = "sort",
                                            size = 16.dp,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Grouped by initial letter
                            val grouped = artists.groupBy { it.name.first().uppercaseChar() }

                            grouped.forEach { (initial, artistList) ->
                                item(key = "header_$initial") {
                                    Text(
                                        text = initial.toString(),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp)
                                    )
                                }

                                items(artistList, key = { it.id }) { artist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedArtistForDetail = artist
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 48dp Circular Avatar (neutral styling, subtle icon/initial)
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = artist.name.take(1).uppercase(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(Modifier.width(14.dp))

                                        // Name and album/song count
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = artist.name,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = "${artist.albumsCount} albums • ${artist.songsCount} songs",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Chevron Right
                                        AppIcon(
                                            name = "chevron_right",
                                            size = 20.dp,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        // Alphabetical A-Z index rail along right edge
                        Column(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight()
                                .padding(end = 4.dp, top = 24.dp, bottom = 96.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            alphabetRail.forEach { letter ->
                                Text(
                                    text = letter,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                3 -> {
                    // "Mini player" Tab in Library section for live customization
                    MiniPlayerCustomizerContent(
                        selectedMiniPlayerStyle = selectedMiniPlayerStyle,
                        onMiniPlayerStyleChanged = onMiniPlayerStyleChanged,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Album Detail Overlay View (Styled identically to Playlist detail)
    if (selectedAlbumForDetail != null) {
        val album = selectedAlbumForDetail!!
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
                // 1. Top Bar: "< Albums" on left, Favorite Heart on right
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
                                .clickable {
                                    selectedAlbumForDetail = null
                                    albumSearchQuery = ""
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
                                text = "Albums",
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
                                .size(64.dp)
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
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${album.artist} · ${album.trackCount} tracks · ${album.year} · On device",
                                fontSize = 13.sp,
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

                // 4. Action Row: "Track order ↓" on left, "Shuffle all" on right
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Track order ↓",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (filteredAlbumTracks.isNotEmpty()) {
                                        val picked = filteredAlbumTracks.random()
                                        onSongSelected(picked.first, album.artist)
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
                items(filteredAlbumTracks) { (trackTitle, trackDuration) ->
                    val isThisPlaying = isPlaying && currentSongTitle == trackTitle
                    val isLiked = likedSongs.any { it.first == trackTitle }

                    DroidLabTrackRow(
                        title = trackTitle,
                        artist = album.artist,
                        duration = trackDuration,
                        isPlaying = isThisPlaying,
                        isLiked = isLiked,
                        onToggleLike = { onToggleLike(trackTitle, album.artist) },
                        onClick = { onSongSelected(trackTitle, album.artist) }
                    )
                }

                // 6. Recommendation section
                item {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = "More from ${album.artist}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "A few listening ideas from your library, inspired by this album.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSongSelected("Drifting Harmony", album.artist) }
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Drifting Harmony",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Solitude",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Listening ideas only · Nothing is added automatically",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    // Artist Detail Overlay View (Styled identically to Playlist detail)
    if (selectedArtistForDetail != null) {
        val artist = selectedArtistForDetail!!
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
                // 1. Top Bar: "< Artists" on left, Favorite Heart on right
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
                                .clickable {
                                    selectedArtistForDetail = null
                                    artistSearchQuery = ""
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
                                text = "Artists",
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
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ("${artist.name.firstOrNull()?.uppercaseChar() ?: "A"}"),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artist.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${artist.albumsCount} albums · ${artist.songsCount} songs · On device",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }

                // 3. Search Bar Pill: "Search artist songs"
                item {
                    OutlinedTextField(
                        value = artistSearchQuery,
                        onValueChange = { artistSearchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search ${artist.name} songs",
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

                // 4. Action Row: "Popular tracks ↓" on left, "Shuffle all" on right
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular tracks ↓",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (filteredArtistTracks.isNotEmpty()) {
                                        val picked = filteredArtistTracks.random()
                                        onSongSelected(picked.first, artist.name)
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
                items(filteredArtistTracks) { (trackTitle, trackDuration) ->
                    val isThisPlaying = isPlaying && currentSongTitle == trackTitle
                    val isLiked = likedSongs.any { it.first == trackTitle }

                    DroidLabTrackRow(
                        title = trackTitle,
                        artist = artist.name,
                        duration = trackDuration,
                        isPlaying = isThisPlaying,
                        isLiked = isLiked,
                        onToggleLike = { onToggleLike(trackTitle, artist.name) },
                        onClick = { onSongSelected(trackTitle, artist.name) }
                    )
                }

                // 6. Recommendation section
                item {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = "More like ${artist.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "A few listening ideas from your library, inspired by this artist.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSongSelected("${artist.name} - Evening Echo", artist.name) }
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Evening Echo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSongSelected("${artist.name} - Skyline Melody", artist.name) }
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Skyline Melody",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Listening ideas only · Nothing is added automatically",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
