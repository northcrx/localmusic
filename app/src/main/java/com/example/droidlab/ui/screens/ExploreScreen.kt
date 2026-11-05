package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTrackRow
import com.example.droidlab.ui.components.SkeletonTrackRow
import kotlinx.coroutines.delay

/**
 * Explore / Search Screen (.cmp-searchpage):
 * - Recent searches & trending recommendations when empty
 * - Real-time skeleton loading animation while typing
 * - Gear / tune modal sheet: Offline (Local Library) vs Online (Spotify, YouTube Music, JioSaavn CDN)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreRoute(
    onNavigateBack: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    val chips = listOf("All", "Songs", "Album", "Artist")

    var isOnlineMode by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("Spotify") }
    var isFilterSheetOpen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val recentSearches = remember {
        mutableStateListOf("Nova Lane", "Chillhop", "Synthwave", "Kyoto")
    }

    val trendingSearches = listOf(
        "Lo-Fi Beats 2024",
        "Acoustic Sunset Chill",
        "Cyberpunk Synth Night",
        "Golden Hour Relax",
        "Ambient Coffee Shop"
    )

    val allTracks = remember {
        listOf(
            Pair("Midnight Drive", "Nova Lane"),
            Pair("Neon Horizon", "Retro Wave"),
            Pair("Blue Bottle Coffee", "Chillhop Beats"),
            Pair("Kyoto Garden", "Zen Echoes"),
            Pair("The Reading Room", "Ambient Works"),
            Pair("Solar Drift", "Cosmic Sound"),
            Pair("Golden Hour", "Acoustic Sunset")
        )
    }

    // Trigger skeleton loading whenever query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isLoading = true
            delay(350)
            isLoading = false
        } else {
            isLoading = false
        }
    }

    val filteredTracks = remember(searchQuery, selectedChip) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allTracks.filter { (title, artist) ->
                title.contains(searchQuery, ignoreCase = true) ||
                artist.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header (.sp-head)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                ) {
                    AppIcon(name = "arrow_back", size = 22.dp)
                }

                Text(
                    text = "Search",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                // Gear / Tune Filter Icon (Tapping opens Online/Offline & Platform Provider)
                IconButton(
                    onClick = { isFilterSheetOpen = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                ) {
                    AppIcon(name = "tune", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Search Bar (.sp-bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = "search",
                        size = 20.dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = if (isOnlineMode) "Search on $selectedPlatform…" else "Search local songs, artists…",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(26.dp)
                        ) {
                            AppIcon(name = "close", size = 16.dp)
                        }
                    }
                }
            }

            // Active Source Indicator Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isOnlineMode) Color(0xFF10B981).copy(alpha = 0.18f) else MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isOnlineMode) "Source: $selectedPlatform (Online)" else "Source: Local Storage (Offline)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnlineMode) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Search Chips (.sp-chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.forEach { chip ->
                    val isSelected = selectedChip == chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                            .clickable { selectedChip = chip }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = chip,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Main Content Area
            if (searchQuery.isBlank()) {
                // Empty query: Show Recent Searches & Trending Recommendations
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Recent Searches
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = { recentSearches.clear() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Clear", fontSize = 11.5.sp)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recentSearches.forEach { query ->
                                    Card(
                                        shape = RoundedCornerShape(99.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        ),
                                        modifier = Modifier.clickable { searchQuery = query }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AppIcon(name = "history", size = 15.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(Modifier.width(6.dp))
                                            Text(text = query, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Trending Recommendations
                    item {
                        Text(
                            text = "Trending Recommendations",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(trendingSearches) { trend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { searchQuery = trend }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(name = "trending_up", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = trend,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            AppIcon(name = "arrow_back", size = 16.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else if (isLoading) {
                // Skeleton Loading State
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(5) {
                        SkeletonTrackRow()
                    }
                }
            } else if (filteredTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found for \"$searchQuery\"",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(filteredTracks) { index, (title, artist) ->
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
                                if (searchQuery !in recentSearches) {
                                    recentSearches.add(0, searchQuery)
                                }
                                onSongSelected(title, artist)
                            }
                        )
                    }
                }
            }
        }
    }

    // Search Source & Platform Provider Modal Bottom Sheet
    if (isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isFilterSheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Search Source & Platform",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configure whether to search local audio files or external CDN streaming platforms.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Mode Selector
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isOnlineMode = false },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !isOnlineMode,
                                onClick = { isOnlineMode = false }
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Offline (Local Library)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Search your device local storage & cache", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isOnlineMode = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isOnlineMode,
                                onClick = { isOnlineMode = true }
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Online (Streaming Platforms)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Search CDN streaming tracks & cloud catalog", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                if (isOnlineMode) {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "Select Streaming Platform CDN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(10.dp))

                    val platforms: List<Pair<String, Color>> = listOf(
                        Pair("Spotify", Color(0xFF1DB954)),
                        Pair("YouTube Music", Color(0xFFFF0000)),
                        Pair("JioSaavn CDN", Color(0xFF2BC5B4)),
                        Pair("SoundCloud", Color(0xFFFF5500))
                    )

                    platforms.forEach { (platform, color) ->
                        val isSelected = selectedPlatform == platform
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            border = if (isSelected) BorderStroke(1.dp, color) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPlatform = platform }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = platform,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    AppIcon(name = "check", size = 18.dp, tint = color)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { isFilterSheetOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply & Close", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
