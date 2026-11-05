package com.example.droidlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabTrackRow

/**
 * Explore / Search Screen matching exact Droid Lab specification (.cmp-searchpage).
 */
@Composable
fun ExploreRoute(
    onNavigateBack: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    val chips = listOf("All", "Songs", "Album", "Artist")

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

    val filteredTracks = remember(searchQuery, selectedChip) {
        if (searchQuery.isBlank()) {
            allTracks
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
                    modifier = Modifier.size(38.dp)
                ) {
                    AppIcon(name = "arrow_back", size = 22.dp)
                }

                Text(
                    text = "Search",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(38.dp)
                ) {
                    AppIcon(name = "tune", size = 20.dp)
                }
            }

            // Search Bar (.sp-bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(46.dp)
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
                                text = "Search songs, files and settings",
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

            // Search Chips (.sp-chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            .padding(horizontal = 14.dp, vertical = 7.dp)
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

            // Search Hint (.sp-hint)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = "lightbulb",
                        size = 18.dp,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Type to search music over here !",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Results List or Empty State
            if (filteredTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results. Try another word.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
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
                                onSongSelected(title, artist)
                            }
                        )
                    }
                }
            }
        }
    }
}
