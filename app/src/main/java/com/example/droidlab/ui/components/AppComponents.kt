package com.example.droidlab.ui.components

import kotlinx.coroutines.delay

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * Top App Bar matching Droid Lab visual specification (.app-bar.elevated).
 * Features uniform semi-transparent 3-dot overflow menu across every screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabTopAppBar(
    title: String,
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onAddClick: (() -> Unit)? = null,
    menuItems: List<Pair<String, String>> = emptyList(),
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isMoreMenuOpen by remember { mutableStateOf(false) }
    var sleepTimerText by remember { mutableStateOf("Off") }

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showScanFoldersDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var selectedSortOption by remember { mutableStateOf("Title (A to Z)") }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4).sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            ) {
                AppIcon(name = "drawer", contentDescription = "Open navigation", size = 22.dp)
            }
        },
        actions = {
            if (onAddClick != null) {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                ) {
                    AppIcon(name = "add", contentDescription = "Add", size = 22.dp)
                }
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            ) {
                AppIcon(name = "search", contentDescription = "Search", size = 20.dp)
            }
            Box {
                IconButton(
                    onClick = { isMoreMenuOpen = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                ) {
                    AppIcon(name = "more_vert", contentDescription = "More actions", size = 22.dp)
                }
                DroidLabMoreMenu(
                    expanded = isMoreMenuOpen,
                    onDismissRequest = { isMoreMenuOpen = false },
                    onOpenSettings = onOpenSettings,
                    onOpenEqualizer = onOpenEqualizer,
                    sleepTimerText = sleepTimerText,
                    onSelectSleepTimer = { showSleepTimerDialog = true },
                    onScanFolders = { showScanFoldersDialog = true },
                    onSortLibrary = { showSortDialog = true },
                    onAbout = { showAboutDialog = true }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )

    // 1. Sleep Timer Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "moon", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("Sleep timer", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val timerOptions = listOf(
                        "Off" to "Off",
                        "15 minutes" to "15m",
                        "30 minutes" to "30m",
                        "45 minutes" to "45m",
                        "60 minutes" to "60m",
                        "End of song" to "Track end"
                    )
                    timerOptions.forEach { (label, value) ->
                        val isSelected = sleepTimerText == value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    sleepTimerText = value
                                    showSleepTimerDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            if (isSelected) {
                                AppIcon(name = "check", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // 2. Scan Music Folders Dialog
    if (showScanFoldersDialog) {
        ScanMusicFoldersDialog(
            onDismissRequest = { showScanFoldersDialog = false }
        )
    }

    // 3. Sort Library Dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "sort", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("Sort library", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val sortOptions = listOf(
                        "Title (A to Z)",
                        "Artist name",
                        "Recently added",
                        "Duration (Longest first)",
                        "Album name"
                    )
                    sortOptions.forEach { opt ->
                        val isSelected = selectedSortOption == opt
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedSortOption = opt
                                    showSortDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                fontSize = 15.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            if (isSelected) {
                                AppIcon(name = "check", size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // 4. About & Version Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "info", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("About & Version", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DroidLab Music",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Version 1.4.2 (Universal Release)",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "High-fidelity local music player built with Jetpack Compose. Features automatic hardware profiling, manual equalizer tuning, and offline playback with zero tracking.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIcon(name = "check_circle", size = 16.dp, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Hardware profiling active",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

/**
 * Tabs row matching Droid Lab visual specification (.tabs-row .tab).
 * Compact vertical padding providing tight, minimal spacing with top app bar.
 */
@Composable
fun DroidLabTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(index) }
                        .padding(top = 7.dp, bottom = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.15.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .fillMaxWidth(0.68f)
                                .clip(RoundedCornerShape(1.25.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
            thickness = 0.75.dp
        )
    }
}

/**
 * Bottom Navigation Bar matching clean content styling.
 * Color matched to content surface (tonalElevation = 0.dp), clean gray/white.
 * Pressing item activates only the compact pill selecting button around the icon,
 * eliminating the oversized rectangular shadow box over the entire item.
 */
@Composable
fun DroidLabBottomNav(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Pair("Home", "home"),
        Pair("Music", "music"),
        Pair("Playlist", "playlist"),
        Pair("Keeps", "keep")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                thickness = 0.65.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (label, icon) ->
                    val isSelected = selectedIndex == index
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onTabSelected(index) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Compact selecting pill indicator (only the icon button highlights)
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                    else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                name = icon,
                                contentDescription = label,
                                size = 20.dp,
                                tint = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Music artwork cover for cards, lists, and player:
 * - Shows authentic Japanese Zen Rock Garden artwork (KyotoGardenArt) for Zen/Kyoto songs
 * - Genre-styled music covers for diverse tracks
 * - Clean music note logo / icon if specific artwork is not available
 */
/**
 * Music artwork cover:
 * - Shows authentic artwork (KyotoGardenArt) for Kyoto Garden / Zen Echoes
 * - By default, if tracks do not have artwork, displays the clean stroked music symbol
 *   in a soft rounded corner box matching the user's screenshot exactly.
 */
@Composable
fun MusicTrackArtCover(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    val cleanTitle = title.lowercase()
    val cleanArtist = artist.lowercase()

    if (cleanTitle.contains("kyoto") || cleanTitle.contains("zen") || cleanArtist.contains("zen")) {
        KyotoGardenArt(modifier = modifier)
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                name = "music",
                size = 22.dp,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}

/**
 * Album artwork cover:
 * - Shows authentic artwork for Zen Echoes
 * - For all other albums, shows the clean album disc icon in a rounded container matching Music -> Albums
 */
@Composable
fun AlbumArtCover(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    val cleanTitle = title.lowercase()
    val cleanArtist = artist.lowercase()

    if (cleanTitle.contains("zen") || cleanArtist.contains("zen")) {
        KyotoGardenArt(modifier = modifier)
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
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
}

/**
 * Clean, compact Floating Mini Music Player matching overall player design:
 * - Squarish rounded corner music artwork box (44.dp, 8.dp radius)
 * - Transparent inner play/pause button (black background removed) with clean progress ring
 * - Crisp typography and matched surface container
 */
@Composable
fun DroidLabMiniPlayer(
    title: String = "Midnight Drive",
    artist: String = "Nova Lane",
    progressPercent: Int = 64,
    isPlaying: Boolean = true,
    isLiked: Boolean = false,
    onToggleLike: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onClick: () -> Unit = {},
    style: String = "Classic Pill",
    showLikeButton: Boolean = true,
    showNextButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cornerRadius = when (style) {
        "Compact Capsule" -> 32.dp
        "Docked Bottom Bar" -> 16.dp
        "Waveform Rhythm" -> 20.dp
        "Glassmorphic Glow" -> 20.dp
        else -> 22.dp
    }

    val cardBg = when (style) {
        "Glassmorphic Glow" -> MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (style == "Docked Bottom Bar") 10.dp else 14.dp)
            .shadow(
                elevation = if (style == "Compact Capsule") 4.dp else 6.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squarish rounded corner music artwork box (matching user's screenshot)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF22242A)),
                contentAlignment = Alignment.Center
            ) {
                if (title.lowercase().contains("kyoto") || artist.lowercase().contains("zen")) {
                    KyotoGardenArt(modifier = Modifier.fillMaxSize())
                } else {
                    AppIcon(
                        name = if (isPlaying) "graphic_eq" else "music",
                        size = 20.dp,
                        tint = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Center details: Title and Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = if (style == "Compact Capsule") 13.sp else 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = artist,
                    fontSize = if (style == "Compact Capsule") 11.sp else 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Waveform Rhythm Visualizer (for Waveform Rhythm style)
            if (style == "Waveform Rhythm") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    val barHeights = if (isPlaying) listOf(14.dp, 22.dp, 10.dp, 18.dp) else listOf(8.dp, 8.dp, 8.dp, 8.dp)
                    barHeights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(2.5.dp)
                                .height(h)
                                .clip(RoundedCornerShape(1.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            // Heart / Like button beside pause and play
            if (showLikeButton) {
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier.size(36.dp)
                ) {
                    AppIcon(
                        name = if (isLiked) "heart" else "heart_border",
                        size = 20.dp,
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(2.dp))

            // Play/Pause button
            if (style == "Docked Bottom Bar") {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(36.dp)
                ) {
                    AppIcon(
                        name = if (isPlaying) "pause" else "play",
                        size = 22.dp,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                // Circular progress ring enclosing play/pause button
                val ringProgress = (progressPercent / 100f).coerceIn(0f, 1f)
                val ringPrimaryColor = MaterialTheme.colorScheme.primary
                val ringTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

                Box(
                    modifier = Modifier
                        .size(if (style == "Compact Capsule") 36.dp else 40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthPx = 2.2.dp.toPx()
                        val radius = (size.minDimension - strokeWidthPx) / 2
                        val centerOffset = Offset(size.width / 2, size.height / 2)

                        drawCircle(
                            color = ringTrackColor,
                            radius = radius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidthPx)
                        )

                        drawArc(
                            color = ringPrimaryColor,
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )
                    }

                    AppIcon(
                        name = if (isPlaying) "pause" else "play",
                        size = if (style == "Compact Capsule") 18.dp else 20.dp,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Skip next button
            if (showNextButton) {
                Spacer(Modifier.width(2.dp))
                IconButton(
                    onClick = onNext,
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

        // Bottom Progress Line (for Docked Bottom Bar)
        if (style == "Docked Bottom Bar") {
            LinearProgressIndicator(
                progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )
        }
    }
}

/**
 * Vinyl Record Canvas Component matching Droid Lab full-player specification.
 */
@Composable
fun VinylRecordPlayer(
    isPlaying: Boolean = true,
    modifier: Modifier = Modifier
) {
    var discRotation by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            var lastTime = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    val dt = (now - lastTime) / 1_000_000_000f
                    lastTime = now
                    discRotation = (discRotation + dt * 55f) % 360f
                }
            }
        }
    }

    val tonearmAngle by animateFloatAsState(
        targetValue = if (isPlaying) 0f else -34f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "tonearm_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vinyl Record Disc
        Box(
            modifier = Modifier
                .size(255.dp)
                .shadow(elevation = 14.dp, shape = CircleShape)
                .graphicsLayer { rotationZ = discRotation }
                .clip(CircleShape)
                .background(Color(0xFF14161A)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val grooveColor = Color(0xFF262932)
                val centerOffset = Offset(size.width / 2, size.height / 2)
                val baseRadius = size.minDimension / 2

                listOf(0.88f, 0.81f, 0.74f, 0.67f, 0.60f, 0.53f, 0.46f).forEach { fraction ->
                    drawCircle(
                        color = grooveColor,
                        radius = baseRadius * fraction,
                        center = centerOffset,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF454B58)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(name = "headset", size = 40.dp, tint = Color.White)

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF14161A))
                )
            }
        }

        // Tonearm / Needle
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
        ) {
            val pivotX = size.width * 0.82f
            val pivotY = size.height * 0.14f

            drawRoundRect(
                color = Color(0xFF4B5563),
                topLeft = Offset(size.width * 0.88f, size.height * 0.32f),
                size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 12.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            drawCircle(color = Color(0xFFBDC2CC), radius = 9.dp.toPx(), center = Offset(pivotX, pivotY))
            drawCircle(color = Color(0xFF4A5568), radius = 5.dp.toPx(), center = Offset(pivotX, pivotY))

            rotate(degrees = tonearmAngle, pivot = Offset(pivotX, pivotY)) {
                drawRoundRect(
                    color = Color(0xFF4A5568),
                    topLeft = Offset(pivotX - 5.dp.toPx(), pivotY - 14.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )

                val armPath = Path().apply {
                    moveTo(pivotX, pivotY)
                    lineTo(size.width * 0.76f, size.height * 0.36f)
                    lineTo(size.width * 0.70f, size.height * 0.52f)
                }
                drawPath(
                    path = armPath,
                    color = Color(0xFFC7CBD3),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                drawRoundRect(
                    color = Color(0xFF374151),
                    topLeft = Offset(size.width * 0.68f, size.height * 0.50f),
                    size = androidx.compose.ui.geometry.Size(11.dp.toPx(), 20.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )

                drawCircle(
                    color = Color(0xFFE5E7EB),
                    radius = 2.dp.toPx(),
                    center = Offset(size.width * 0.70f, size.height * 0.52f)
                )
            }
        }
    }
}

/**
 * Clean, preset-based Sleep Timer Dialog with active state indicator and cancel action.
 */
@Composable
fun SleepTimerDialog(
    activeMinutes: Int?,
    onDismissRequest: () -> Unit,
    onTimerSet: (Int?) -> Unit
) {
    val presets = listOf(
        Pair("15 minutes", 15),
        Pair("30 minutes", 30),
        Pair("45 minutes", 45),
        Pair("60 minutes", 60),
        Pair("End of current track", 4)
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(name = "timer", size = 20.dp, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Sleep Timer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (activeMinutes != null && activeMinutes > 0) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                name = "check_circle",
                                size = 18.dp,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Active: stops playback in $activeMinutes min",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = {
                                    onTimerSet(null)
                                    onDismissRequest()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Turn Off",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }

                Text(
                    text = "Select when to pause playback:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { (label, mins) ->
                        val isSelected = activeMinutes == mins
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onTimerSet(mins)
                                    onDismissRequest()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    AppIcon(
                                        name = "check",
                                        size = 18.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Close", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

/**
 * Authentic Japanese Zen Rock Garden Artwork:
 * Draws the serene rock garden matching user screenshot with 3 rocks and circular ripple patterns.
 */
@Composable
fun KyotoGardenArt(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Soft gravel sand background
        drawRect(color = Color(0xFFE4DFD7))

        // Fine raked horizontal sand grooves
        val lineSpacing = 6.dp.toPx()
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color(0xFFD3CCC1),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.2.dp.toPx()
            )
            y += lineSpacing
        }

        // Concentric ripples around Rock 1 (Bottom Left, main rock)
        val r1 = Offset(w * 0.38f, h * 0.65f)
        for (i in 1..8) {
            drawCircle(
                color = Color(0xFFBEB5A5),
                radius = 32.dp.toPx() + i * 8.5.dp.toPx(),
                center = r1,
                style = Stroke(width = 1.4.dp.toPx())
            )
        }

        // Concentric ripples around Rock 2 (Top Center)
        val r2 = Offset(w * 0.50f, h * 0.28f)
        for (i in 1..6) {
            drawCircle(
                color = Color(0xFFBEB5A5),
                radius = 22.dp.toPx() + i * 8.dp.toPx(),
                center = r2,
                style = Stroke(width = 1.4.dp.toPx())
            )
        }

        // Concentric ripples around Rock 3 (Center Right)
        val r3 = Offset(w * 0.74f, h * 0.34f)
        for (i in 1..5) {
            drawCircle(
                color = Color(0xFFBEB5A5),
                radius = 16.dp.toPx() + i * 7.5.dp.toPx(),
                center = r3,
                style = Stroke(width = 1.4.dp.toPx())
            )
        }

        // Draw Rock 1 (organic dark stone)
        val rock1 = Path().apply {
            moveTo(r1.x - 40.dp.toPx(), r1.y + 18.dp.toPx())
            cubicTo(r1.x - 46.dp.toPx(), r1.y - 12.dp.toPx(), r1.x - 22.dp.toPx(), r1.y - 38.dp.toPx(), r1.x, r1.y - 40.dp.toPx())
            cubicTo(r1.x + 28.dp.toPx(), r1.y - 34.dp.toPx(), r1.x + 42.dp.toPx(), r1.y - 10.dp.toPx(), r1.x + 36.dp.toPx(), r1.y + 20.dp.toPx())
            cubicTo(r1.x + 20.dp.toPx(), r1.y + 34.dp.toPx(), r1.x - 25.dp.toPx(), r1.y + 30.dp.toPx(), r1.x - 40.dp.toPx(), r1.y + 18.dp.toPx())
            close()
        }
        drawPath(rock1, color = Color(0xFF2C2D30))
        drawCircle(color = Color(0xFF45474D), radius = 18.dp.toPx(), center = Offset(r1.x - 8.dp.toPx(), r1.y - 8.dp.toPx()))

        // Draw Rock 2
        val rock2 = Path().apply {
            moveTo(r2.x - 26.dp.toPx(), r2.y + 12.dp.toPx())
            cubicTo(r2.x - 30.dp.toPx(), r2.y - 16.dp.toPx(), r2.x - 12.dp.toPx(), r2.y - 26.dp.toPx(), r2.x + 4.dp.toPx(), r2.y - 28.dp.toPx())
            cubicTo(r2.x + 22.dp.toPx(), r2.y - 22.dp.toPx(), r2.x + 28.dp.toPx(), r2.y + 8.dp.toPx(), r2.x + 16.dp.toPx(), r2.y + 18.dp.toPx())
            close()
        }
        drawPath(rock2, color = Color(0xFF2C2D30))
        drawCircle(color = Color(0xFF45474D), radius = 12.dp.toPx(), center = Offset(r2.x - 4.dp.toPx(), r2.y - 6.dp.toPx()))

        // Draw Rock 3
        val rock3 = Path().apply {
            moveTo(r3.x - 18.dp.toPx(), r3.y + 8.dp.toPx())
            cubicTo(r3.x - 20.dp.toPx(), r3.y - 12.dp.toPx(), r3.x, r3.y - 18.dp.toPx(), r3.x + 10.dp.toPx(), r3.y - 16.dp.toPx())
            cubicTo(r3.x + 22.dp.toPx(), r3.y - 8.dp.toPx(), r3.x + 20.dp.toPx(), r3.y + 12.dp.toPx(), r3.x, r3.y + 14.dp.toPx())
            close()
        }
        drawPath(rock3, color = Color(0xFF2C2D30))
        drawCircle(color = Color(0xFF45474D), radius = 8.dp.toPx(), center = Offset(r3.x - 2.dp.toPx(), r3.y - 4.dp.toPx()))
    }
}

/**
 * Clean & Elegant Music Player matching the user's reference screenshot:
 * - Top Bar: Chevron down, Now Playing / From your device, 3-dots more menu
 * - Large Album Artwork with rounded corners (Kyoto Garden zen rock garden)
 * - Track title, Artist, and Heart like button
 * - Clean progress bar with elapsed & remaining time
 * - Playback controls: Shuffle, Previous, Large Play/Pause circle, Next, Repeat
 * - Audio format & device status
 * - Bottom action buttons: Equalizer, Queue (12 songs), Sleep timer (crescent moon)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabNowPlayingScreen(
    title: String = "Kyoto Garden",
    artist: String = "Zen Echoes",
    album: String = "Zen Echoes",
    progress: Float = 0.35f,
    currentTime: String = "1:28",
    totalTime: String = "4:12",
    isPlaying: Boolean = true,
    isLiked: Boolean = false,
    activeDeviceName: String = "Phone Speaker",
    activeDeviceIcon: String = "phone_speaker",
    onOpenEqualizer: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onClose: () -> Unit = {},
    onSongSelected: (title: String, artist: String) -> Unit = { _, _ -> },
    onGoToArtist: (String) -> Unit = {},
    onGoToAlbum: (String, String) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit = {},
    sleepTimerMinutes: Int? = null,
    onSetSleepTimer: (Int?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    val isDark = isSystemInDarkTheme()
    var currentProgress by remember { mutableFloatStateOf(progress) }
    var isShuffleOn by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }
    var isOptionsSheetOpen by remember { mutableStateOf(false) }
    var showFileDetailsDialog by remember { mutableStateOf(false) }
    var isQueueOpen by remember { mutableStateOf(false) }
    var isSleepTimerDialogOpen by remember { mutableStateOf(false) }

    if (isQueueOpen) {
        DroidLabQueueScreen(
            currentTitle = title,
            currentArtist = artist,
            isPlaying = isPlaying,
            isLiked = isLiked,
            currentTime = currentTime,
            totalTime = totalTime,
            progress = currentProgress,
            onPlayPause = onPlayPause,
            onToggleLike = onToggleLike,
            onSongSelected = { newTitle, newArtist ->
                onSongSelected(newTitle, newArtist)
            },
            onClose = { isQueueOpen = false }
        )
    } else {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(name = "chevron_down", size = 24.dp, tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Now playing",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "From your device",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { isOptionsSheetOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(name = "more_horiz", size = 24.dp, tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // 2. Large Album Artwork with rounded corners matching screenshot
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    MusicTrackArtCover(
                        title = title,
                        artist = artist,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. Song Title, Artist & Like Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = artist,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier.size(42.dp)
                    ) {
                        AppIcon(
                            name = if (isLiked) "heart" else "heart_border",
                            size = 26.dp,
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 4. Progress Bar & Timestamps
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    currentProgress = (offset.x / size.width).coerceIn(0f, 1f)
                                }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(currentProgress)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isDark) Color(0xFF8FA3B8) else Color(0xFF4A5568))
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentTime,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = totalTime,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 5. Playback Controls Row: Shuffle, Previous, Large Play/Pause circle, Next, Repeat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isShuffleOn = !isShuffleOn },
                        modifier = Modifier.size(44.dp)
                    ) {
                        AppIcon(
                            name = "shuffle",
                            size = 23.dp,
                            tint = if (isShuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(48.dp)
                    ) {
                        AppIcon(
                            name = "skip_previous",
                            size = 30.dp,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Center Large Circular Play/Pause Button matching screenshot
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF434E5F) else Color(0xFF4A5568))
                            .clickable(onClick = onPlayPause),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            name = if (isPlaying) "pause" else "play",
                            size = 32.dp,
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(48.dp)
                    ) {
                        AppIcon(
                            name = "skip_next",
                            size = 30.dp,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { isRepeatOn = !isRepeatOn },
                        modifier = Modifier.size(44.dp)
                    ) {
                        AppIcon(
                            name = "repeat",
                            size = 23.dp,
                            tint = if (isRepeatOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 6. Source & Audio Format Line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppIcon(
                        name = "storage",
                        size = 14.dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "FLAC • 44.1 kHz • On device",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 7. Bottom Actions: Equalizer, Queue, Sleep Timer (Moon)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenEqualizer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        AppIcon(
                            name = "tune",
                            size = 22.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .clickable { isQueueOpen = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            name = "queue",
                            size = 18.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Queue • 12 songs",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { isSleepTimerDialogOpen = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        AppIcon(
                            name = "bedtime",
                            size = 22.dp,
                            tint = if (sleepTimerMinutes != null && sleepTimerMinutes > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for 3-dots more menu matching Screenshot 2
    if (isOptionsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isOptionsSheetOpen = false },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                )
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 20.dp)
            ) {
                // Header inside sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (title == "Kyoto Garden") {
                            KyotoGardenArt(modifier = Modifier.fillMaxSize())
                        } else {
                            AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "$artist • On device",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { isOptionsSheetOpen = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        AppIcon(name = "close", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(4.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                Spacer(Modifier.height(4.dp))

                // 1. Remove from liked songs / Add to liked songs
                PlayerSheetOptionItem(
                    icon = if (isLiked) "heart" else "heart_border",
                    text = if (isLiked) "Remove from liked songs" else "Add to liked songs",
                    onClick = {
                        onToggleLike()
                        isOptionsSheetOpen = false
                    }
                )

                // 2. Add to playlist
                PlayerSheetOptionItem(
                    icon = "playlist_add",
                    text = "Add to playlist",
                    onClick = { isOptionsSheetOpen = false }
                )

                // 3. Play next
                PlayerSheetOptionItem(
                    icon = "queue",
                    text = "Play next",
                    onClick = { isOptionsSheetOpen = false }
                )

                // 4. Go to artist
                PlayerSheetOptionItem(
                    icon = "person",
                    text = "Go to artist",
                    hasChevron = true,
                    onClick = {
                        isOptionsSheetOpen = false
                        onClose()
                        onGoToArtist(artist)
                    }
                )

                // 5. Go to album
                PlayerSheetOptionItem(
                    icon = "album",
                    text = "Go to album",
                    hasChevron = true,
                    onClick = {
                        isOptionsSheetOpen = false
                        onClose()
                        onGoToAlbum(album, artist)
                    }
                )

                // 6. File details
                PlayerSheetOptionItem(
                    icon = "info",
                    text = "File details",
                    hasChevron = true,
                    onClick = {
                        isOptionsSheetOpen = false
                        showFileDetailsDialog = true
                    }
                )

                // 7. Settings
                PlayerSheetOptionItem(
                    icon = "settings",
                    text = "Settings",
                    hasChevron = true,
                    onClick = {
                        isOptionsSheetOpen = false
                        onClose()
                        onOpenSettings()
                    }
                )
            }
        }
    }

    // Sleep Timer Dialog
    if (isSleepTimerDialogOpen) {
        SleepTimerDialog(
            activeMinutes = sleepTimerMinutes,
            onDismissRequest = { isSleepTimerDialogOpen = false },
            onTimerSet = onSetSleepTimer
        )
    }

    // File Details Dialog
    if (showFileDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showFileDetailsDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(name = "info", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("File Details", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val details = listOf(
                        "Title" to title,
                        "Artist" to artist,
                        "Album" to album,
                        "Format" to "FLAC 24-bit / 96.0 kHz",
                        "Bitrate" to "1411 kbps",
                        "File Size" to "34.8 MB",
                        "Duration" to totalTime,
                        "Path" to "/storage/emulated/0/Music/${album}/${title.replace(" ", "_").lowercase()}.flac"
                    )
                    details.forEach { (label, value) ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFileDetailsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

}

/**
 * Clean Row Item for Player 3-dot Bottom Sheet
 */
@Composable
fun PlayerSheetOptionItem(
    icon: String,
    text: String,
    hasChevron: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            name = icon,
            size = 22.dp,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (hasChevron) {
            AppIcon(
                name = "chevron_right",
                size = 18.dp,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Dedicated Queue Screen matching user reference screenshot:
 * - Top Bar: "Queue" title, subtitle "12 songs • From your device", down chevron button
 * - NOW PLAYING section: Album thumbnail, title, artist, like button, pause/play, progress bar
 * - Up next • 11 section header with "Clear queue"
 * - Reorderable list of queued songs with 6-dot drag handles, artwork, duration, and remove (x) button
 */
@Composable
fun DroidLabQueueScreen(
    currentTitle: String,
    currentArtist: String,
    isPlaying: Boolean,
    isLiked: Boolean,
    currentTime: String = "1:28",
    totalTime: String = "4:12",
    progress: Float = 0.35f,
    onPlayPause: () -> Unit,
    onToggleLike: () -> Unit,
    onSongSelected: (title: String, artist: String) -> Unit,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)

    var queueList by remember {
        mutableStateOf(
            listOf(
                Triple("Blue Bottle Coffee", "Chillhop Beats", "3:45"),
                Triple("The Reading Room", "Ambient Works", "5:08"),
                Triple("After the Rain", "Zen Echoes", "3:56"),
                Triple("Sunday, Slowly", "Chillhop Beats", "2:48"),
                Triple("Paper Lanterns", "Ambient Works", "4:36"),
                Triple("Evening Light", "Zen Echoes", "4:02")
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 1. Top Header: Title "Queue", subtitle "12 songs • From your device", down chevron button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Queue",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${queueList.size + 1} songs • From your device",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(name = "chevron_down", size = 22.dp, tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(18.dp))

            // 2. NOW PLAYING Section Header
            Text(
                text = "NOW PLAYING",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(8.dp))

            // 3. Now Playing Card matching screenshot
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentTitle == "Kyoto Garden") {
                                KyotoGardenArt(modifier = Modifier.fillMaxSize())
                            } else {
                                AppIcon(name = "music", size = 24.dp)
                            }
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = currentArtist,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = onToggleLike, modifier = Modifier.size(36.dp)) {
                            AppIcon(
                                name = if (isLiked) "heart" else "heart_border",
                                size = 22.dp,
                                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = onPlayPause, modifier = Modifier.size(36.dp)) {
                            AppIcon(
                                name = if (isPlaying) "pause" else "play",
                                size = 22.dp,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF4A5568),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = currentTime, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = totalTime, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // 4. Up next • X + Clear queue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Up next • ${queueList.size}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Clear queue",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { queueList = emptyList() }
                )
            }

            Spacer(Modifier.height(6.dp))

            // 5. Queued Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(queueList, key = { it.first }) { (title, artist, duration) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongSelected(title, artist) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Drag Handle (6-dot icon)
                        AppIcon(
                            name = "drag_handle",
                            size = 20.dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        Spacer(Modifier.width(12.dp))

                        // Square track art thumbnail with subtle music note
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = artist,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = duration,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.width(6.dp))

                        IconButton(
                            onClick = { queueList = queueList.filter { it.first != title } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(name = "close", size = 16.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Outlined Play Triangle (▷) matching exact reference in Image 3.
 */
@Composable
fun OutlinedPlayTriangle(
    tint: Color = Color(0xFF1E222D),
    size: Dp = 24.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.32f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.50f)
            lineTo(w * 0.32f, h * 0.78f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = 2.4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Outlined Pause Bars (||) matching exact reference in Image 3.
 */
@Composable
fun OutlinedPauseBars(
    tint: Color = Color(0xFF1E222D),
    size: Dp = 24.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val barWidth = 3.2.dp.toPx()
        val barHeight = h * 0.52f
        val topY = (h - barHeight) / 2
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.34f - barWidth / 2, topY),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.66f - barWidth / 2, topY),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
    }
}

/**
 * Stroke Previous Track Icon (|<) matching exact reference in Image 3.
 */
@Composable
fun StrokePreviousIcon(
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.2.dp.toPx()
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.24f),
            end = Offset(w * 0.22f, h * 0.76f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        val path = Path().apply {
            moveTo(w * 0.78f, h * 0.24f)
            lineTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.78f, h * 0.76f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Stroke Next Track Icon (>|) matching exact reference in Image 3.
 */
@Composable
fun StrokeNextIcon(
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.24f)
            lineTo(w * 0.68f, h * 0.50f)
            lineTo(w * 0.22f, h * 0.76f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.78f, h * 0.24f),
            end = Offset(w * 0.78f, h * 0.76f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Stroke Shuffle Icon (crossed arrows) matching exact reference in Image 3.
 */
@Composable
fun StrokeShuffleIcon(
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.dp.toPx()

        val path1 = Path().apply {
            moveTo(w * 0.18f, h * 0.32f)
            cubicTo(w * 0.45f, h * 0.32f, w * 0.55f, h * 0.68f, w * 0.76f, h * 0.68f)
        }
        drawPath(path1, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        val arrow1 = Path().apply {
            moveTo(w * 0.64f, h * 0.56f)
            lineTo(w * 0.80f, h * 0.68f)
            lineTo(w * 0.64f, h * 0.80f)
        }
        drawPath(arrow1, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.68f)
            cubicTo(w * 0.45f, h * 0.68f, w * 0.55f, h * 0.32f, w * 0.76f, h * 0.32f)
        }
        drawPath(path2, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        val arrow2 = Path().apply {
            moveTo(w * 0.64f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.32f)
            lineTo(w * 0.64f, h * 0.44f)
        }
        drawPath(arrow2, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Stroke Repeat Icon (loop arrows) matching exact reference in Image 3.
 */
@Composable
fun StrokeRepeatIcon(
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.dp.toPx()

        val topPath = Path().apply {
            moveTo(w * 0.22f, h * 0.55f)
            lineTo(w * 0.22f, h * 0.34f)
            cubicTo(w * 0.22f, h * 0.26f, w * 0.28f, h * 0.26f, w * 0.38f, h * 0.26f)
            lineTo(w * 0.76f, h * 0.26f)
        }
        drawPath(topPath, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        val arrowTop = Path().apply {
            moveTo(w * 0.64f, h * 0.16f)
            lineTo(w * 0.80f, h * 0.26f)
            lineTo(w * 0.64f, h * 0.36f)
        }
        drawPath(arrowTop, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val botPath = Path().apply {
            moveTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.66f)
            cubicTo(w * 0.78f, h * 0.74f, w * 0.72f, h * 0.74f, w * 0.62f, h * 0.74f)
            lineTo(w * 0.24f, h * 0.74f)
        }
        drawPath(botPath, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        val arrowBot = Path().apply {
            moveTo(w * 0.36f, h * 0.64f)
            lineTo(w * 0.20f, h * 0.74f)
            lineTo(w * 0.36f, h * 0.84f)
        }
        drawPath(arrowBot, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Dynamic Top Bar 3-dot Dropdown Menu matching screenshot specification:
 * - Semi-transparent background
 * - Rounded corners (22.dp)
 * - Identical options on every screen:
 *   1. Settings
 *   2. Equalizer & Sound
 *   3. Sleep timer (Off)
 *   4. Scan music folders
 *   5. Sort library
 *   [Divider]
 *   6. About & Version (v1.4.2)
 */
@Composable
fun DroidLabMoreMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    sleepTimerText: String = "Off",
    onSelectSleepTimer: () -> Unit = {},
    onScanFolders: () -> Unit = {},
    onSortLibrary: () -> Unit = {},
    onAbout: () -> Unit = {},
    items: List<Pair<String, String>> = emptyList(),
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (expanded) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(x = -16, y = 56),
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                ),
                modifier = modifier.width(235.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // 1. Settings
                    DroidLabMoreMenuItem(
                        iconName = "settings",
                        title = "Settings",
                        onClick = {
                            onDismissRequest()
                            onOpenSettings()
                        }
                    )

                    // 2. Equalizer & Sound
                    DroidLabMoreMenuItem(
                        iconName = "tune",
                        title = "Equalizer & Sound",
                        onClick = {
                            onDismissRequest()
                            onOpenEqualizer()
                        }
                    )

                    // 3. Sleep timer
                    DroidLabMoreMenuItem(
                        iconName = "moon",
                        title = "Sleep timer",
                        trailingText = sleepTimerText,
                        onClick = {
                            onDismissRequest()
                            onSelectSleepTimer()
                        }
                    )

                    // 4. Scan music folders
                    DroidLabMoreMenuItem(
                        iconName = "folder",
                        title = "Scan music folders",
                        onClick = {
                            onDismissRequest()
                            onScanFolders()
                        }
                    )

                    // 5. Sort library
                    DroidLabMoreMenuItem(
                        iconName = "sort",
                        title = "Sort library",
                        onClick = {
                            onDismissRequest()
                            onSortLibrary()
                        }
                    )

                    // Thin subtle horizontal divider
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        thickness = 0.75.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )

                    // 6. About & Version
                    DroidLabMoreMenuItem(
                        iconName = "info",
                        title = "About & Version",
                        trailingText = "v1.4.2",
                        onClick = {
                            onDismissRequest()
                            onAbout()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Single-layer Popup Menu for track options across every screen.
 * Options: Play next, Add to playlist, Like/Unlike, Go to album, Go to artist.
 */
@Composable
fun DroidLabTrackOptionsMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    artist: String,
    isLiked: Boolean = false,
    onPlayNext: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    onGoToAlbum: () -> Unit = {},
    onGoToArtist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (expanded) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(x = -14, y = 36),
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                ),
                modifier = modifier.width(220.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    DroidLabMoreMenuItem(
                        iconName = "queue",
                        title = "Play next",
                        onClick = {
                            onDismissRequest()
                            onPlayNext()
                        }
                    )
                    DroidLabMoreMenuItem(
                        iconName = "playlist_add",
                        title = "Add to playlist",
                        onClick = {
                            onDismissRequest()
                            onAddToPlaylist()
                        }
                    )
                    DroidLabMoreMenuItem(
                        iconName = if (isLiked) "heart" else "heart_border",
                        title = if (isLiked) "Unlike" else "Add to Liked",
                        onClick = {
                            onDismissRequest()
                            onToggleLike()
                        }
                    )
                    DroidLabMoreMenuItem(
                        iconName = "album",
                        title = "Go to album",
                        onClick = {
                            onDismissRequest()
                            onGoToAlbum()
                        }
                    )
                    DroidLabMoreMenuItem(
                        iconName = "person",
                        title = "Go to artist",
                        onClick = {
                            onDismissRequest()
                            onGoToArtist()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DroidLabMoreMenuItem(
    iconName: String,
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            name = iconName,
            size = 20.dp,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
            )
        }
    }
}

/**
 * Navigation Drawer Content matching Droid Lab (.ui-drawer).
 */
@Composable
fun DroidLabDrawerSheet(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(310.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AppIcon(name = "close", size = 22.dp)
                }
            }

            Spacer(Modifier.height(16.dp))

            val navItems = listOf(
                Triple("Home", "home", "home"),
                Triple("Music", "music", "music"),
                Triple("Playlists", "playlist", "playlist"),
                Triple("Keeps", "keep", "keep"),
                Triple("Settings", "settings", "settings")
            )

            navItems.forEach { (label, icon, route) ->
                val isSelected = currentRoute == route
                NavigationDrawerItem(
                    label = { Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                    icon = { AppIcon(name = icon, size = 22.dp) },
                    selected = isSelected,
                    onClick = {
                        onNavigate(route)
                        onClose()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Track Row matching Droid Lab (.track-row / .list-item).
 */
@Composable
fun DroidLabTrackRow(
    index: Int? = null,
    title: String,
    artist: String,
    duration: String = "3:30",
    isPlaying: Boolean = false,
    isLiked: Boolean = false,
    onToggleLike: (() -> Unit)? = null,
    onPlayNext: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onGoToAlbum: () -> Unit = {},
    onGoToArtist: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showTrackMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Squarish rounded corner music artwork (no 123 numbers!)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            MusicTrackArtCover(
                title = title,
                artist = artist,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(14.dp))

        // Title and artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = artist,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration text
        Text(
            text = duration,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(6.dp))

        // Action button: When playing, turns into rhythm sync animation icon. When paused, 3-dots.
        Box {
            IconButton(
                onClick = { showTrackMenu = true },
                modifier = Modifier.size(36.dp)
            ) {
                if (isPlaying) {
                    AppIcon(
                        name = "graphic_eq",
                        size = 20.dp,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    AppIcon(
                        name = "more_vert",
                        size = 20.dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DroidLabTrackOptionsMenu(
                expanded = showTrackMenu,
                onDismissRequest = { showTrackMenu = false },
                title = title,
                artist = artist,
                isLiked = isLiked,
                onPlayNext = {
                    onClick()
                    onPlayNext()
                },
                onAddToPlaylist = onAddToPlaylist,
                onToggleLike = {
                    onToggleLike?.invoke()
                },
                onGoToAlbum = onGoToAlbum,
                onGoToArtist = onGoToArtist
            )
        }
    }
}

/**
 * Shimmering skeleton track row for search loading state.
 */
@Composable
fun SkeletonTrackRow() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(11.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha * 0.7f))
            )
        }
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha * 0.7f))
        )
    }
}


/**
 * Interactive Scan Music Folders Dialog with folder selection checkboxes
 * and animated 0-100% indexing progress.
 */
@Composable
fun ScanMusicFoldersDialog(
    onDismissRequest: () -> Unit,
    onStartScan: ((Set<String>?) -> Unit)? = null,
    scanProgress: Float = 0f,
    isScanning: Boolean = false,
    currentFileName: String = "",
    isScanFinished: Boolean = false,
    totalFound: Int = 0
) {
    var selectedFolders by remember {
        mutableStateOf(
            setOf(
                "Music",
                "Download",
                "Podcasts"
            )
        )
    }
    val allFolders = remember {
        listOf(
            "Music" to "Music folder • MP3, FLAC, M4A",
            "Download" to "Downloads • Audio files",
            "Podcasts" to "Podcasts • Voice & Shows",
            "Audiobooks" to "Spoken audio & books"
        )
    }
    var localIsScanning by remember { mutableStateOf(false) }
    var localScanProgress by remember { mutableFloatStateOf(0f) }
    var localCurrentScannedFile by remember { mutableStateOf("") }
    var localIsScanFinished by remember { mutableStateOf(false) }

    val activeIsScanning = if (onStartScan != null) isScanning else localIsScanning
    val activeScanProgress = if (onStartScan != null) scanProgress else localScanProgress
    val activeCurrentFileName = if (onStartScan != null) currentFileName else localCurrentScannedFile
    val activeIsScanFinished = if (onStartScan != null) isScanFinished else localIsScanFinished

    val scanFiles = remember {
        listOf(
            "kyoto_garden.flac",
            "midnight_drive.wav",
            "blue_bottle_coffee.mp3",
            "the_reading_room.flac",
            "neon_horizon.flac",
            "solar_drift.wav",
            "golden_hour.mp3",
            "rainy_cafe.flac",
            "velvet_moon.flac"
        )
    }

    LaunchedEffect(localIsScanning) {
        if (localIsScanning && onStartScan == null) {
            localScanProgress = 0f
            localIsScanFinished = false
            for (i in 1..20) {
                delay(100)
                localScanProgress = i / 20f
                localCurrentScannedFile = scanFiles[(i - 1) % scanFiles.size]
            }
            localIsScanning = false
            localIsScanFinished = true
        }
    }

    AlertDialog(
        onDismissRequest = { if (!activeIsScanning) onDismissRequest() },
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(name = "folder", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text("Scan Music Folders", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!activeIsScanning && !activeIsScanFinished) {
                    Text(
                        text = "Select folders to index music library:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    allFolders.forEach { (path, info) ->
                        val isChecked = selectedFolders.contains(path)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedFolders = if (isChecked) {
                                        selectedFolders - path
                                    } else {
                                        selectedFolders + path
                                    }
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedFolders = if (checked) selectedFolders + path else selectedFolders - path
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = path,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = info,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else if (activeIsScanning) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { activeScanProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Indexing audio...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(activeScanProgress * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Reading: $activeCurrentFileName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(name = "check", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Scan Complete! (100%)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        val trackCount = if (onStartScan != null) totalFound else (if (selectedFolders.size == 4) 73 else selectedFolders.size * 18 + 10)
                        Text(
                            text = "Indexed $trackCount songs across ${selectedFolders.size} selected folders. All tracks are synchronized and ready to play.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (activeIsScanning) {
                // scanning
            } else if (activeIsScanFinished) {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            } else {
                Button(
                    onClick = { if (onStartScan != null) onStartScan(selectedFolders) else localIsScanning = true },
                    enabled = selectedFolders.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Scan Now")
                }
            }
        },
        dismissButton = {
            if (!activeIsScanning) {
                TextButton(onClick = onDismissRequest) {
                    Text(if (activeIsScanFinished) "Close" else "Cancel")
                }
            }
        }
    )
}
