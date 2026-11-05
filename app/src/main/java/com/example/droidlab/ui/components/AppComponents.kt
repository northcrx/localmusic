package com.example.droidlab.ui.components

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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.cos
import kotlin.math.sin

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
                        .padding(top = 13.dp, bottom = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.15.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth(0.72f)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            thickness = 1.dp
        )
    }
}

/**
 * Bottom Navigation Bar matching Droid Lab visual specification (.nav-bar.nav-material).
 * Provides generous breathing room for system gesture controllers.
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
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 8.dp)
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (label, icon) ->
                    val isSelected = selectedIndex == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onTabSelected(index) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Pill icon indicator
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                name = icon,
                                contentDescription = label,
                                size = 21.dp,
                                tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
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
 * High-attention Floating Mini Music Player (.mini-ring).
 * Sleek glowing accent border, gradient avatar, and circular progress ring with play/pause.
 */
@Composable
fun DroidLabMiniPlayer(
    title: String = "Midnight Drive",
    artist: String = "Nova Lane",
    progressPercent: Int = 64,
    isPlaying: Boolean = true,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            .border(
                BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E222D)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient Headphone Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(name = "headset", size = 22.dp, tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            // Center details with mini animated equalizer bars
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isPlaying) {
                        Spacer(Modifier.width(8.dp))
                        MiniAnimatedWaveform()
                    }
                }
                Text(
                    text = artist,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$progressPercent% played",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }

            // Circular progress ring enclosing circular play/pause button
            val ringProgress = (progressPercent / 100f).coerceIn(0f, 1f)
            val ringPrimaryColor = MaterialTheme.colorScheme.primary
            val ringTrackColor = ringPrimaryColor.copy(alpha = 0.22f)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 3.2.dp.toPx()
                    val radius = (size.minDimension - strokeWidthPx) / 2
                    val centerOffset = Offset(size.width / 2, size.height / 2)

                    // Track circle
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

                // Inner circular play/pause button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ringPrimaryColor)
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = if (isPlaying) "pause" else "play",
                        size = 18.dp,
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Skip next button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    name = "skip_next",
                    size = 22.dp,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Bouncing mini equalizer wave bars.
 */
@Composable
fun MiniAnimatedWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(16.dp)
    ) {
        Box(Modifier.width(2.5.dp).height(h1.dp).clip(RoundedCornerShape(1.dp)).background(MaterialTheme.colorScheme.primary))
        Box(Modifier.width(2.5.dp).height(h2.dp).clip(RoundedCornerShape(1.dp)).background(MaterialTheme.colorScheme.primary))
        Box(Modifier.width(2.5.dp).height(h3.dp).clip(RoundedCornerShape(1.dp)).background(MaterialTheme.colorScheme.primary))
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
    // Rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
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
                .shadow(elevation = 16.dp, shape = CircleShape)
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

        // Tonearm / Needle on top right
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
        ) {
            val pivotX = size.width * 0.82f
            val pivotY = size.height * 0.14f

            // Pivot base
            drawCircle(color = Color(0xFFBDC2CC), radius = 9.dp.toPx(), center = Offset(pivotX, pivotY))
            drawCircle(color = Color(0xFF4A5568), radius = 5.dp.toPx(), center = Offset(pivotX, pivotY))

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
        }
    }
}

/**
 * Interactive Clock Dial Sleep Timer Dialog.
 */
@Composable
fun SleepTimerDialog(
    onDismissRequest: () -> Unit,
    onTimerSet: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(30) }
    val presets = listOf(15, 30, 45, 60, 90)

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(name = "timer", size = 24.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Sleep Timer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Circular Clock Dial representation
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.minDimension / 2 - 12.dp.toPx()

                        // Clock ring
                        drawCircle(
                            color = Color(0xFF3A4254),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // 12 ticks
                        for (i in 0 until 12) {
                            val angleRad = Math.toRadians((i * 30 - 90).toDouble())
                            val startX = center.x + (radius - 8.dp.toPx()) * cos(angleRad).toFloat()
                            val startY = center.y + (radius - 8.dp.toPx()) * sin(angleRad).toFloat()
                            val endX = center.x + radius * cos(angleRad).toFloat()
                            val endY = center.y + radius * sin(angleRad).toFloat()
                            drawLine(
                                color = Color.White.copy(alpha = 0.6f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        // Clock hand pointing according to selected minutes (0 to 60 mapped to 360)
                        val handAngle = Math.toRadians(((selectedMinutes % 60) * 6 - 90).toDouble())
                        val handX = center.x + (radius * 0.65f) * cos(handAngle).toFloat()
                        val handY = center.y + (radius * 0.65f) * sin(handAngle).toFloat()
                        drawLine(
                            color = Color(0xFF4F80FF),
                            start = center,
                            end = Offset(handX, handY),
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(color = Color(0xFF4F80FF), radius = 6.dp.toPx(), center = center)
                    }

                    // Display time
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 44.dp)
                    ) {
                        Text(
                            text = "$selectedMinutes min",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    presets.forEach { mins ->
                        val isSelected = selectedMinutes == mins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedMinutes = mins }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "App will gently fade out music and close when the timer completes.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onTimerSet(selectedMinutes)
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Set Timer", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * True Full-Screen Now Playing Modal Sheet matching exact Droid Lab Vinyl specification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabNowPlayingSheet(
    visible: Boolean,
    title: String = "Midnight Drive",
    artist: String = "Nova Lane",
    album: String = "Neon Hours",
    progress: Float = 0.42f,
    currentTime: String = "1:48",
    totalTime: String = "4:12",
    isPlaying: Boolean = true,
    onPlayPause: () -> Unit = { },
    onPrevious: () -> Unit = { },
    onNext: () -> Unit = { },
    onDismiss: () -> Unit = { },
    modifier: Modifier = Modifier
) {
    if (!visible) return

    var currentProgress by remember { mutableFloatStateOf(progress) }
    var isShuffleOn by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }

    var isSlidersMenuOpen by remember { mutableStateOf(false) }
    var isSleepTimerDialogOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
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
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(name = "chevron_down", size = 22.dp)
                }

                Spacer(Modifier.weight(1f))

                // Output Device Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = "phone_speaker",
                        size = 16.dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Phone Speaker",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.weight(1f))

                // Sliders / 3-Line Tune Icon with Dropdown Menu
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
                            .width(175.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Equalizer", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { AppIcon(name = "equalizer", size = 20.dp) },
                            onClick = {
                                isSlidersMenuOpen = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sleep Timer", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { AppIcon(name = "timer", size = 20.dp) },
                            onClick = {
                                isSlidersMenuOpen = false
                                isSleepTimerDialogOpen = true
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Vinyl Record Art with Tonearm
            VinylRecordPlayer(isPlaying = isPlaying)

            Spacer(Modifier.height(18.dp))

            // Metadata
            Text(
                text = "NOW PLAYING • VINYL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = "$artist • $album",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(18.dp))

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

            Spacer(Modifier.height(12.dp))

            // Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { isShuffleOn = !isShuffleOn },
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = "shuffle",
                        size = 20.dp,
                        tint = if (isShuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPrevious),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(name = "skip_previous", size = 26.dp)
                }

                // Center large Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .shadow(elevation = 10.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = if (isPlaying) "pause" else "play",
                        size = 28.dp,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onNext),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(name = "skip_next", size = 26.dp)
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { isRepeatOn = !isRepeatOn },
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        name = "repeat",
                        size = 20.dp,
                        tint = if (isRepeatOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(Modifier.height(14.dp))

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
            onDismissRequest = { isSleepTimerDialogOpen = false },
            onTimerSet = { _ -> }
        )
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
