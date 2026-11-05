package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

/**
 * Home Screen matching exact Droid Lab specification with 4-box shuffle grid.
 */
@Composable
fun HomeRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("For you", "Following", "Top Tracks")

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

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Home",
                onMenuClick = onOpenDrawer,
                onSearchClick = onOpenSearch
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
                                    // Shuffle circular button on top
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
                val displayList = when (selectedTabIndex) {
                    1 -> trackList.reversed()
                    2 -> trackList.take(4)
                    else -> trackList
                }

                itemsIndexed(displayList) { index, (title, artist) ->
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
    }
}
