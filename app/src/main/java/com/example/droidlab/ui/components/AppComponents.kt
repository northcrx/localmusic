package com.example.droidlab.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.theme.*

/**
 * Top App Bar matching Droid Lab visual specification (.app-bar.elevated).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabTopAppBar(
    title: String,
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
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
                modifier = Modifier.size(42.dp)
            ) {
                AppIcon(name = "drawer", contentDescription = "Open navigation", size = 22.dp)
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(42.dp)
            ) {
                AppIcon(name = "search", contentDescription = "Search", size = 20.dp)
            }
            Box {
                IconButton(
                    onClick = { isMoreMenuOpen = true },
                    modifier = Modifier.size(42.dp)
                ) {
                    AppIcon(name = "more_vert", contentDescription = "More actions", size = 22.dp)
                }
                DroidLabMoreMenu(
                    expanded = isMoreMenuOpen,
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
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (label, icon) ->
                    val isSelected = selectedIndex == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(index) }
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Pill icon indicator
                        Box(
                            modifier = Modifier
                                .width(58.dp)
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
 * Floating Mini Music Player matching exact Droid Lab circular progress ring specification (.mini-ring).
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
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left circular avatar with headphone icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4B5160)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(name = "headset", size = 22.dp, tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Circular progress ring enclosing play/pause button
            val ringProgress = (progressPercent / 100f).coerceIn(0f, 1f)
            val ringPrimaryColor = MaterialTheme.colorScheme.primary
            val ringTrackColor = ringPrimaryColor.copy(alpha = 0.2f)

            Box(
                modifier = Modifier.size(48.dp),
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

                // Inner play/pause circular button
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
}

/**
 * Vinyl Record Canvas Component matching Droid Lab full-player specification.
 */
@Composable
fun VinylRecordPlayer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vinyl Record
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

            // Center Label Disc
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
 * Full-screen Now Playing Modal Sheet matching exact Droid Lab Vinyl specification.
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier.fillMaxHeight(0.96f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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

                // Sliders / Tune Icon
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(38.dp)
                ) {
                    AppIcon(name = "tune", size = 22.dp)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Vinyl Record Art
            VinylRecordPlayer()

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
                IconButton(onClick = { isShuffleOn = !isShuffleOn }) {
                    AppIcon(
                        name = "shuffle",
                        size = 20.dp,
                        tint = if (isShuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onPrevious) {
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

                IconButton(onClick = onNext) {
                    AppIcon(name = "skip_next", size = 26.dp)
                }

                IconButton(onClick = { isRepeatOn = !isRepeatOn }) {
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
}

/**
 * Top Bar 3-dot Dropdown Menu matching Droid Lab (.ui-menu).
 */
@Composable
fun DroidLabMoreMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onActionClick: (String) -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
    ) {
        DropdownMenuItem(
            text = { Text("Analyze", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
            leadingIcon = { AppIcon(name = "analyze", size = 20.dp) },
            onClick = {
                onActionClick("Analyze")
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text("Manage", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
            leadingIcon = { AppIcon(name = "manage", size = 20.dp) },
            onClick = {
                onActionClick("Manage")
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text("Equalizer", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
            leadingIcon = { AppIcon(name = "equalizer", size = 20.dp) },
            onClick = {
                onActionClick("Equalizer")
                onDismissRequest()
            }
        )
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
 * Search Overlay matching Droid Lab (.ui-search).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidLabSearchOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filterChips = listOf("All", "Music", "Saved", "Files")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    AppIcon(name = "arrow_back", size = 22.dp)
                }
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search pages, blocks and actions…",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        AppIcon(name = "close", size = 18.dp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Guide Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        name = "tips_and_updates",
                        size = 20.dp,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Type to search screens, blocks and menu actions. Pick a filter chip to narrow things down.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 4.dp)
                )
                filterChips.forEach { chip ->
                    val isSelected = selectedFilter == chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                            .clickable { selectedFilter = chip }
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

            Spacer(Modifier.height(32.dp))

            // Body Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isEmpty()) "Start typing to search" else "No results for \"$query\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
