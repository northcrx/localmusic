package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.MusicTrackArtCover
import com.example.droidlab.ui.theme.ManropeFontFamily
import com.example.droidlab.ui.theme.SpaceGroteskFontFamily

/**
 * Settings Screen matching user's exact specification & Screenshot 1:
 * - Top header: large "Settings" title with circular back button on top-right
 * - Sections:
 *   1. LIBRARY & PLAYER: Local library, Appearance & album art, Playback
 *   2. DISCOVERY: Ideas based on likes (Switch), Optional online search (Switch)
 *   3. LOCK SCREEN & NOTIFICATIONS: Show media controls (Switch)
 *   4. MINI PLAYER CUSTOMIZATION & PREVIEW: Interactive live preview of the current
 *      mini player design with customizable style options (Circular progress ring vs
 *      bottom progress bar, like button toggle, corner radius).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    selectedFontPreference: String = "Manrope",
    onFontPreferenceChanged: (String) -> Unit = {}
) {
    var isAppearanceOpen by remember { mutableStateOf(false) }

    // Settings States
    var ideasBasedOnLikes by remember { mutableStateOf(true) }
    var optionalOnlineSearch by remember { mutableStateOf(false) }
    var showMediaControls by remember { mutableStateOf(true) }

    // Mini Player Customization States
    var miniPlayerStyle by remember { mutableStateOf("Circular Ring") } // "Circular Ring" or "Bottom Bar"
    var showLikeInMiniPlayer by remember { mutableStateOf(true) }
    var showNextInMiniPlayer by remember { mutableStateOf(true) }
    var isPreviewPlaying by remember { mutableStateOf(true) }
    var isPreviewLiked by remember { mutableStateOf(true) }
    var previewProgress by remember { mutableFloatStateOf(0.68f) }

    if (isAppearanceOpen) {
        // Appearance Settings Subscreen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Appearance",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { isAppearanceOpen = false },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            AppIcon(name = "arrow_back", size = 22.dp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Font Selector Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(name = "text_fields", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Select App Font",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Changes the typography across the entire application",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            val fontOptions = listOf(
                                Triple(
                                    "Manrope",
                                    "Default Droid Lab typeface • Clean modern sans-serif",
                                    ManropeFontFamily
                                ),
                                Triple(
                                    "Space Grotesk",
                                    "Geometric Grotesque typeface with distinct aesthetic",
                                    SpaceGroteskFontFamily
                                ),
                                Triple(
                                    "System Font",
                                    "Native Android system font matching global device settings",
                                    FontFamily.Default
                                )
                            )

                            fontOptions.forEachIndexed { index, (name, description, family) ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                val isSelected = selectedFontPreference == name

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onFontPreferenceChanged(name) }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onFontPreferenceChanged(name) }
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            fontSize = 15.sp,
                                            fontFamily = family,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = description,
                                            fontSize = 12.sp,
                                            fontFamily = family,
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
    } else {
        // Main Settings Screen matching Screenshot 1
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Circular back button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            name = "chevron_left",
                            size = 20.dp,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // 1. Section: LIBRARY & PLAYER
                item {
                    Column {
                        Text(
                            text = "LIBRARY & PLAYER",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Item 1: Local library
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "folder",
                                        size = 22.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Local library",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "3 folders · Scan now or scan at launch",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    AppIcon(
                                        name = "chevron_right",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Item 2: Appearance & album art
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isAppearanceOpen = true }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "tips_and_updates",
                                        size = 22.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Appearance & album art",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "$selectedFontPreference · Show album artwork",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    AppIcon(
                                        name = "chevron_right",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Item 3: Playback
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "tune",
                                        size = 22.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Playback",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Gapless on · Equalizer · Sleep timer",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    AppIcon(
                                        name = "chevron_right",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Section: DISCOVERY
                item {
                    Column {
                        Text(
                            text = "DISCOVERY",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Item 1: Ideas based on likes
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "heart_border",
                                        size = 22.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Ideas based on likes",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Show related listening ideas in Liked songs",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = ideasBasedOnLikes,
                                        onCheckedChange = { ideasBasedOnLikes = it }
                                    )
                                }

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Item 2: Optional online search
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        name = "public",
                                        size = 22.dp,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Optional online search",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Off · Your local files stay separate",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = optionalOnlineSearch,
                                        onCheckedChange = { optionalOnlineSearch = it }
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Section: LOCK SCREEN & NOTIFICATIONS
                item {
                    Column {
                        Text(
                            text = "LOCK SCREEN & NOTIFICATIONS",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(
                                    name = "notifications",
                                    size = 22.dp,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Show media controls",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "Album art, track, progress & playback",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = showMediaControls,
                                    onCheckedChange = { showMediaControls = it }
                                )
                            }
                        }
                    }
                }

                // 4. Section: MINI PLAYER CUSTOMIZATION & LIVE PREVIEW
                item {
                    Column {
                        Text(
                            text = "MINI PLAYER CUSTOMIZATION",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Progress Indicator Style",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Option 1: Circular Progress Ring
                                    FilterChip(
                                        selected = miniPlayerStyle == "Circular Ring",
                                        onClick = { miniPlayerStyle = "Circular Ring" },
                                        label = { Text("Circular Ring") },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Option 2: Bottom Progress Bar
                                    FilterChip(
                                        selected = miniPlayerStyle == "Bottom Bar",
                                        onClick = { miniPlayerStyle = "Bottom Bar" },
                                        label = { Text("Bottom Bar") },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                // Toggle Like Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Show Favorite Heart Button",
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Switch(
                                        checked = showLikeInMiniPlayer,
                                        onCheckedChange = { showLikeInMiniPlayer = it }
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Toggle Next Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Show Next Track Button",
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Switch(
                                        checked = showNextInMiniPlayer,
                                        onCheckedChange = { showNextInMiniPlayer = it }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Interactive Live Mini Player Preview
                        Text(
                            text = "LIVE PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )

                        // Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 3.dp, shape = RoundedCornerShape(22.dp))
                                .clip(RoundedCornerShape(22.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(22.dp)
                                )
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Squarish rounded artwork cover
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        MusicTrackArtCover(
                                            title = "Kyoto Garden",
                                            artist = "Zen Echoes",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    // Title & Artist
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Kyoto Garden",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Zen Echoes",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }

                                    // Like button
                                    if (showLikeInMiniPlayer) {
                                        IconButton(
                                            onClick = { isPreviewLiked = !isPreviewLiked },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            AppIcon(
                                                name = if (isPreviewLiked) "heart" else "heart_border",
                                                size = 20.dp,
                                                tint = if (isPreviewLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Play / Pause Control
                                    if (miniPlayerStyle == "Circular Ring") {
                                        // Circular Progress Ring around Play/Pause
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable { isPreviewPlaying = !isPreviewPlaying },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val ringTrack = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                            val ringActive = MaterialTheme.colorScheme.primary

                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val strokeW = 2.4.dp.toPx()
                                                drawCircle(
                                                    color = ringTrack,
                                                    style = Stroke(width = strokeW)
                                                )
                                                drawArc(
                                                    color = ringActive,
                                                    startAngle = -90f,
                                                    sweepAngle = 360f * previewProgress,
                                                    useCenter = false,
                                                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                                                )
                                            }

                                            AppIcon(
                                                name = if (isPreviewPlaying) "pause" else "play",
                                                size = 19.dp,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    } else {
                                        // Standard Button (with bottom progress bar)
                                        IconButton(
                                            onClick = { isPreviewPlaying = !isPreviewPlaying },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            AppIcon(
                                                name = if (isPreviewPlaying) "pause" else "play",
                                                size = 22.dp,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Next button
                                    if (showNextInMiniPlayer) {
                                        Spacer(Modifier.width(2.dp))
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            AppIcon(
                                                name = "skip_next",
                                                size = 22.dp,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                // If Bottom Bar style is selected, draw the bottom progress line
                                if (miniPlayerStyle == "Bottom Bar") {
                                    LinearProgressIndicator(
                                        progress = { previewProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
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
