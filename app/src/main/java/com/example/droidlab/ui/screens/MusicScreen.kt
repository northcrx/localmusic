package com.example.droidlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.droidlab.ui.components.DroidLabTabs
import com.example.droidlab.ui.components.DroidLabTopAppBar
import com.example.droidlab.ui.components.DroidLabTrackRow

/**
 * Music Screen matching exact Droid Lab specification (.screen-page[data-page-label="Music"]).
 */
@Composable
fun MusicRoute(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> }
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("For you", "Following", "Popular")

    val sampleTracks = listOf(
        Pair("Blue Bottle Coffee", "Chillhop Beats"),
        Pair("Kyoto Garden", "Zen Echoes"),
        Pair("The Reading Room", "Ambient Works"),
        Pair("Midnight Drive", "Synthwave City"),
        Pair("Neon Horizon", "Retro Wave"),
        Pair("Rainy Cafe", "Lo-Fi Study"),
        Pair("Velvet Moon", "Jazz Quartet")
    )

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Music",
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

            val displayList = when (selectedTabIndex) {
                1 -> sampleTracks.reversed()
                2 -> sampleTracks.take(4)
                else -> sampleTracks
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
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
