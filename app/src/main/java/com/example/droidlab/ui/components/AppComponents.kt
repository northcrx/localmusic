package com.example.droidlab.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Top App Bar matching Droid Lab visual specification (.app-bar.elevated).
 * Supports contextual 3-dot dropdown menu items per screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabTopAppBar(
    title: String,
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    menuItems: List<Pair<String, String>> = listOf(
        Pair("Analyze", "analyze"),
        Pair("Manage", "manage"),
        Pair("Equalizer", "equalizer")
    ),
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isMoreMenuOpen by remember { mutableStateOf(false) }

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
                    items = menuItems,
                    onDismissRequest = { isMoreMenuOpen = false },
                    onActionClick = onActionClick
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
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
 * Bottom Navigation Bar matching Droid Lab visual specification (.nav-bar.nav-material).
 * Clean, compact height with system gesture bar padding and zero extra bloat.
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
        Pair("Search", "search"),
        Pair("Keeps", "keep")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                thickness = 0.75.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (label, icon) ->
                    val isSelected = selectedIndex == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onTabSelected(index) }
                            .padding(vertical = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Pill icon indicator
                        Box(
                            modifier = Modifier
                                .width(52.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                name = icon,
                                contentDescription = label,
                                size = 20.dp,
                                tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
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
 * Clean, compact Floating Mini Music Player matching exact reference Image 2.
 * Subtle container, dark circular avatar (#4A5060) with white headset, crisp typography,
 * and circular progress ring with dark inner play circle.
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
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val containerBg = if (isDark) {
        Color(0xCC202430) // Translucent dark slate, showing backside content
    } else {
        Color(0xDDE6EAF4) // Translucent light slate, showing backside content
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .border(
                BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerBg
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dark Slate Headphone Avatar matching Image 2
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF454B58)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(name = "headset", size = 20.dp, tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            // Center details: Title and Artist (crisp, neat, single lines)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = artist,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Heart / Like button beside pause and play on the left side
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier.size(34.dp)
            ) {
                AppIcon(
                    name = if (isLiked) "heart" else "heart_border",
                    size = 20.dp,
                    tint = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(2.dp))

            // Circular progress ring enclosing dark circular play/pause button
            val ringProgress = (progressPercent / 100f).coerceIn(0f, 1f)
            val ringPrimaryColor = Color(0xFF454B58)
            val ringTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 2.4.dp.toPx()
                    val radius = (size.minDimension - strokeWidthPx) / 2
                    val centerOffset = Offset(size.width / 2, size.height / 2)

                    // Subtle Track ring
                    drawCircle(
                        color = ringTrackColor,
                        radius = radius,
                        center = centerOffset,
                        style = Stroke(width = strokeWidthPx)
                    )

                    // Progress arc
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

                // Inner dark circular play/pause button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(ringPrimaryColor)
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val w = size.width
                            val h = size.height
                            val barW = 2.4.dp.toPx()
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.28f - barW / 2, 0f),
                                size = androidx.compose.ui.geometry.Size(barW, h),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
                            )
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.72f - barW / 2, 0f),
                                size = androidx.compose.ui.geometry.Size(barW, h),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
                            )
                        }
                    } else {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = Path().apply {
                                moveTo(w * 0.25f, 0f)
                                lineTo(w * 0.88f, h * 0.5f)
                                lineTo(w * 0.25f, h)
                                close()
                            }
                            drawPath(path = path, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.width(4.dp))

            // Skip next button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                StrokeNextIcon(
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 20.dp
                )
            }
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
    // Disc continuous spin rotation - freezes smoothly in place when paused
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

    // Tonearm movement:
    // When isPlaying == true: tonearm is at 0f (on the record, playing)
    // When isPlaying == false: tonearm rotates away to -34f onto its rest cradle
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
            // Concentric vinyl grooves
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

            // Center Label Disc with Headset Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF454B58)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(name = "headset", size = 40.dp, tint = Color.White)

                // Center spindle hole
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF14161A))
                )
            }
        }

        // Tonearm / Needle on top right with animated swing onto/away from the CD
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
        ) {
            val pivotX = size.width * 0.82f
            val pivotY = size.height * 0.14f

            // Tonearm rest cradle (where tonearm parks when paused)
            drawRoundRect(
                color = Color(0xFF4B5563),
                topLeft = Offset(size.width * 0.88f, size.height * 0.32f),
                size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 12.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            // Stationary Pivot Base on the turntable deck
            drawCircle(color = Color(0xFFBDC2CC), radius = 9.dp.toPx(), center = Offset(pivotX, pivotY))
            drawCircle(color = Color(0xFF4A5568), radius = 5.dp.toPx(), center = Offset(pivotX, pivotY))

            // Rotatable Tonearm assembly
            rotate(degrees = tonearmAngle, pivot = Offset(pivotX, pivotY)) {
                // Counterweight behind pivot
                drawRoundRect(
                    color = Color(0xFF4A5568),
                    topLeft = Offset(pivotX - 5.dp.toPx(), pivotY - 14.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )

                // Arm line bending towards the record
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

                // Headshell / cartridge needle at the end
                drawRoundRect(
                    color = Color(0xFF374151),
                    topLeft = Offset(size.width * 0.68f, size.height * 0.50f),
                    size = androidx.compose.ui.geometry.Size(11.dp.toPx(), 20.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )

                // Needle stylus point
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
                // Header
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

                // Active status banner if already running
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

                // Presets list
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

                // Bottom actions
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
 * True Full-Screen Now Playing Screen (0dp corners, edge-to-edge).
 * Features clean stroked music control icons matching Image 3.
 */
@Composable
fun DroidLabNowPlayingScreen(
    title: String = "Midnight Drive",
    artist: String = "Nova Lane",
    album: String = "Neon Hours",
    progress: Float = 0.42f,
    currentTime: String = "1:48",
    totalTime: String = "4:12",
    isPlaying: Boolean = true,
    isLiked: Boolean = false,
    activeDeviceName: String = "Phone Speaker",
    activeDeviceIcon: String = "phone_speaker",
    onOpenEqualizer: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    onPlayPause: () -> Unit = { },
    onPrevious: () -> Unit = { },
    onNext: () -> Unit = { },
    onClose: () -> Unit = { },
    sleepTimerMinutes: Int? = null,
    onSetSleepTimer: (Int?) -> Unit = { },
    modifier: Modifier = Modifier
) {
    // Intercept back gesture to close full screen
    BackHandler(onBack = onClose)

    var currentProgress by remember { mutableFloatStateOf(progress) }
    var isShuffleOn by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }

    var isSlidersMenuOpen by remember { mutableStateOf(false) }
    var isSleepTimerDialogOpen by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Down Chevron
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(name = "chevron_down", size = 22.dp)
                }

                Spacer(Modifier.weight(1f))

                // Output Device Pill (Tap to open Equalizer & Sound Profile)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .clickable(onClick = onOpenEqualizer)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = activeDeviceIcon,
                        size = 15.dp,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = activeDeviceName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.weight(1f))

                // Sleep timer badge if active + Tune Sliders Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (sleepTimerMinutes != null && sleepTimerMinutes > 0) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { isSleepTimerDialogOpen = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                name = "timer",
                                size = 13.dp,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${sleepTimerMinutes}m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                    }

                    Box {
                        IconButton(
                            onClick = { isSlidersMenuOpen = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                        ) {
                            AppIcon(name = "tune", size = 22.dp)
                        }

                        DropdownMenu(
                            expanded = isSlidersMenuOpen,
                            onDismissRequest = { isSlidersMenuOpen = false },
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Equalizer", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = { AppIcon(name = "equalizer", size = 20.dp) },
                                onClick = {
                                    isSlidersMenuOpen = false
                                    onOpenEqualizer()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    val label = if (sleepTimerMinutes != null && sleepTimerMinutes > 0) {
                                        "Sleep Timer (${sleepTimerMinutes}m)"
                                    } else {
                                        "Sleep Timer"
                                    }
                                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                },
                                leadingIcon = { AppIcon(name = "timer", size = 20.dp) },
                                onClick = {
                                    isSlidersMenuOpen = false
                                    isSleepTimerDialogOpen = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Vinyl Record Art with Tonearm
            VinylRecordPlayer(isPlaying = isPlaying)

            Spacer(Modifier.height(14.dp))

            // Metadata
            Text(
                text = "NOW PLAYING • VINYL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = "$artist • $album",
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(16.dp))

            // Progress Slider
            Slider(
                value = currentProgress,
                onValueChange = { currentProgress = it },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = currentTime, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = totalTime, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))

            // Playback Controls Row matching exact stroke reference in Image 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button (Stroke crossed arrows)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { isShuffleOn = !isShuffleOn },
                    contentAlignment = Alignment.Center
                ) {
                    StrokeShuffleIcon(
                        tint = if (isShuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        size = 22.dp
                    )
                }

                // Previous Button: |< stroke icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPrevious),
                    contentAlignment = Alignment.Center
                ) {
                    StrokePreviousIcon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        size = 26.dp
                    )
                }

                // Heart / Like Button (beside pause and play on the left side)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleLike),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = if (isLiked) "heart" else "heart_border",
                        size = 24.dp,
                        tint = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Center Large White Play/Pause Button with Clean Outlined Triangle (▷)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        OutlinedPauseBars(
                            tint = Color(0xFF1E222D),
                            size = 24.dp
                        )
                    } else {
                        OutlinedPlayTriangle(
                            tint = Color(0xFF1E222D),
                            size = 24.dp
                        )
                    }
                }

                // Next Button: >| stroke icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onNext),
                    contentAlignment = Alignment.Center
                ) {
                    StrokeNextIcon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        size = 26.dp
                    )
                }

                // Repeat Button (Stroke loop arrows)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { isRepeatOn = !isRepeatOn },
                    contentAlignment = Alignment.Center
                ) {
                    StrokeRepeatIcon(
                        tint = if (isRepeatOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        size = 22.dp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                thickness = 0.75.dp
            )

            Spacer(Modifier.height(10.dp))

            // Bottom Actions: Lyrics & Up Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(name = "lyrics", size = 18.dp, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Lyrics", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(name = "queue", size = 18.dp, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Up next (12)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
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
        // Left vertical stroke line
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.24f),
            end = Offset(w * 0.22f, h * 0.76f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Left-pointing triangle
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
        // Right-pointing triangle
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
        // Right vertical stroke line
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

        // Upper to lower line
        val path1 = Path().apply {
            moveTo(w * 0.18f, h * 0.32f)
            cubicTo(w * 0.45f, h * 0.32f, w * 0.55f, h * 0.68f, w * 0.76f, h * 0.68f)
        }
        drawPath(path1, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        // Arrowhead 1
        val arrow1 = Path().apply {
            moveTo(w * 0.64f, h * 0.56f)
            lineTo(w * 0.80f, h * 0.68f)
            lineTo(w * 0.64f, h * 0.80f)
        }
        drawPath(arrow1, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Lower to upper line
        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.68f)
            cubicTo(w * 0.45f, h * 0.68f, w * 0.55f, h * 0.32f, w * 0.76f, h * 0.32f)
        }
        drawPath(path2, color = tint, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        // Arrowhead 2
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

        // Top line pointing right
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

        // Bottom line pointing left
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
 * Dynamic Top Bar 3-dot Dropdown Menu matching Droid Lab (.ui-menu).
 */
@Composable
fun DroidLabMoreMenu(
    expanded: Boolean,
    items: List<Pair<String, String>> = listOf(
        Pair("Analyze", "analyze"),
        Pair("Manage", "manage"),
        Pair("Equalizer", "equalizer")
    ),
    onDismissRequest: () -> Unit,
    onActionClick: (String) -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(185.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        items.forEach { (label, iconName) ->
            DropdownMenuItem(
                text = { Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                leadingIcon = { AppIcon(name = iconName, size = 20.dp) },
                onClick = {
                    onActionClick(label)
                    onDismissRequest()
                }
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
            // Close Button
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

            // Navigation Items
            val navItems = listOf(
                Triple("Home", "home", "home"),
                Triple("Music", "music", "music"),
                Triple("Search", "search", "explore"),
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
    index: Int,
    title: String,
    artist: String,
    duration: String = "3:30",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$index",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(name = "music", size = 20.dp, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
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
