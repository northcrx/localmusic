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
 * Keep Screen matching exact Droid Lab specification (.screen-page[data-page-label="Keep"]).
 */
@Composable
fun KeepsRoute(
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
        Pair("Midnight Drive", "Synthwave City")
    )

    Scaffold(
        topBar = {
            DroidLabTopAppBar(
                title = "Keep",
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
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(sampleTracks) { index, (title, artist) ->
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
