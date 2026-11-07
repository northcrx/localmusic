package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.components.DroidLabMiniPlayer
import com.example.droidlab.ui.components.MusicTrackArtCover
import com.example.droidlab.ui.theme.ManropeFontFamily
import com.example.droidlab.ui.theme.SpaceGroteskFontFamily

/**
 * Settings Screen matching user's exact specification:
 * - Consistent aesthetic across all screens and inside sub-screens:
 *   - Large title on top left + circular back button `<` on top right
 *   - Rounded cards (RoundedCornerShape(22.dp)) with subtle surfaceVariant background
 *   - Uppercase small bold category headers
 * - Sections in main view:
 *   1. LIBRARY & PLAYER: Local library, Appearance & album art, Playback, Mini player
 *   2. DISCOVERY: Ideas based on likes (Switch), Optional online search (Switch)
 *   3. LOCK SCREEN & NOTIFICATIONS: Show media controls (Switch)
 * - Subscreens for inside buttons:
 *   - "Mini player" subscreen: 5 advanced yet clean variations with live interactive preview & controls
 *   - "Appearance" subscreen: Font selection & artwork display
 *   - "Local library" subscreen: Folder locations & scanner
 *   - "Playback" subscreen: Gapless audio & equalizer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    selectedFontPreference: String = "Manrope",
    onFontPreferenceChanged: (String) -> Unit = {},
    selectedMiniPlayerStyle: String = "Classic Pill",
    onMiniPlayerStyleChanged: (String) -> Unit = {}
) {
    // Subscreen navigation state: null = Main Settings, "Appearance", "MiniPlayer", "LocalLibrary", "Playback"
    var activeSubscreen by remember { mutableStateOf<String?>(null) }

    // Setting states
    var ideasBasedOnLikes by remember { mutableStateOf(true) }
    var optionalOnlineSearch by remember { mutableStateOf(false) }
    var showMediaControls by remember { mutableStateOf(true) }
    var showLikeInMiniPlayer by remember { mutableStateOf(true) }
    var showNextInMiniPlayer by remember { mutableStateOf(true) }

    // Live preview states
    var isPreviewPlaying by remember { mutableStateOf(true) }
    var isPreviewLiked by remember { mutableStateOf(true) }

    when (activeSubscreen) {
        "MiniPlayer" -> {
            // Dedicated Mini Player Customization Subscreen (5 Variations + Live Interactive Preview)
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
                            text = "Mini Player",
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
                                .clickable { activeSubscreen = null },
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
                MiniPlayerCustomizerContent(
                    selectedMiniPlayerStyle = selectedMiniPlayerStyle,
                    onMiniPlayerStyleChanged = onMiniPlayerStyleChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
        "Appearance" -> {
            // Appearance Subscreen matching main settings design
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
                            text = "Appearance",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { activeSubscreen = null },
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
                    item {
                        Column {
                            Text(
                                text = "SELECT APP FONT",
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
                                    val fontOptions = listOf(
                                        Triple("Manrope", "Default Droid Lab typeface • Clean modern sans-serif", ManropeFontFamily),
                                        Triple("Space Grotesk", "Geometric Grotesque typeface with distinct aesthetic", SpaceGroteskFontFamily),
                                        Triple("System Font", "Native Android system font matching global device settings", FontFamily.Default)
                                    )

                                    fontOptions.forEachIndexed { index, (name, description, family) ->
                                        if (index > 0) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )
                                        }

                                        val isSelected = selectedFontPreference == name

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onFontPreferenceChanged(name) }
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
            }
        }
        "LocalLibrary" -> {
            // Local Library Subscreen matching main settings design
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
                            text = "Local Library",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { activeSubscreen = null },
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
                    item {
                        Column {
                            Text(
                                text = "SCANNED FOLDERS",
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
                                    val folders = listOf(
                                        Pair("/storage/emulated/0/Music", "42 tracks • 320 kbps MP3 / FLAC"),
                                        Pair("/storage/emulated/0/Download/Music", "18 tracks • High Resolution"),
                                        Pair("/storage/emulated/0/Android/media", "9 tracks • Offline Audio")
                                    )

                                    folders.forEachIndexed { index, (path, detail) ->
                                        if (index > 0) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AppIcon(name = "folder", size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = path, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                                                Spacer(Modifier.height(2.dp))
                                                Text(text = detail, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        "Playback" -> {
            // Playback Subscreen matching main settings design
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
                            text = "Playback",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { activeSubscreen = null },
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
                    item {
                        Column {
                            Text(
                                text = "AUDIO ENGINE",
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
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "Gapless Playback", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "Eliminate silence between continuous tracks", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(checked = true, onCheckedChange = {})
                                    }

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "Equalizer Audio Processing", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "Bass boost and hardware device profiling active", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(checked = true, onCheckedChange = {})
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else -> {
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
                    // 1. Section: LIBRARY & PLAYER (Screenshot 1)
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
                                            .clickable { activeSubscreen = "LocalLibrary" }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "folder", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Local library", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "3 folders · Scan now or scan at launch", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        AppIcon(name = "chevron_right", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                                    // Item 2: Appearance & album art
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeSubscreen = "Appearance" }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "tips_and_updates", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Appearance & album art", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "$selectedFontPreference · Show album artwork", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        AppIcon(name = "chevron_right", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                                    // Item 3: Playback
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeSubscreen = "Playback" }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "tune", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Playback", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "Gapless on · Equalizer · Sleep timer", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        AppIcon(name = "chevron_right", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                                    // Item 4: Mini player (NEW!)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeSubscreen = "MiniPlayer" }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "graphic_eq", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Mini player", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "$selectedMiniPlayerStyle · 5 design variations", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        AppIcon(name = "chevron_right", size = 18.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }

                    // 2. Section: DISCOVERY (Screenshot 1)
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
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "heart_border", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Ideas based on likes", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "Show related listening ideas in Liked songs", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(checked = ideasBasedOnLikes, onCheckedChange = { ideasBasedOnLikes = it })
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(name = "public", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Optional online search", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(text = "Off · Your local files stay separate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(checked = optionalOnlineSearch, onCheckedChange = { optionalOnlineSearch = it })
                                    }
                                }
                            }
                        }
                    }

                    // 3. Section: LOCK SCREEN & NOTIFICATIONS (Screenshot 1)
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
                                    AppIcon(name = "notifications", size = 22.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Show media controls", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(Modifier.height(2.dp))
                                        Text(text = "Album art, track, progress & playback", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(checked = showMediaControls, onCheckedChange = { showMediaControls = it })
                                }
                            }
                        }
                    }

                    // 4. Quick Mini Player Live Preview on Main Settings (Screenshot 1)
                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CURRENT MINI PLAYER STYLE",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    letterSpacing = 0.8.sp
                                )

                                Text(
                                    text = "Customize >",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { activeSubscreen = "MiniPlayer" }
                                )
                            }

                            DroidLabMiniPlayer(
                                title = "Kyoto Garden",
                                artist = "Zen Echoes",
                                progressPercent = 64,
                                isPlaying = isPreviewPlaying,
                                isLiked = isPreviewLiked,
                                onToggleLike = { isPreviewLiked = !isPreviewLiked },
                                onPlayPause = { isPreviewPlaying = !isPreviewPlaying },
                                onNext = { },
                                style = selectedMiniPlayerStyle,
                                showLikeButton = showLikeInMiniPlayer,
                                showNextButton = showNextInMiniPlayer,
                                onClick = { activeSubscreen = "MiniPlayer" },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MiniPlayerCustomizerContent(
    selectedMiniPlayerStyle: String,
    onMiniPlayerStyleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPreviewPlaying by remember { mutableStateOf(true) }
    var isPreviewLiked by remember { mutableStateOf(true) }
    var showLikeInMiniPlayer by remember { mutableStateOf(true) }
    var showNextInMiniPlayer by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // 1. Live Interactive Preview
        item {
            Text(
                text = "LIVE INTERACTIVE PREVIEW",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            DroidLabMiniPlayer(
                title = "Kyoto Garden",
                artist = "Zen Echoes",
                progressPercent = 68,
                isPlaying = isPreviewPlaying,
                isLiked = isPreviewLiked,
                onToggleLike = { isPreviewLiked = !isPreviewLiked },
                onPlayPause = { isPreviewPlaying = !isPreviewPlaying },
                onNext = { },
                style = selectedMiniPlayerStyle,
                showLikeButton = showLikeInMiniPlayer,
                showNextButton = showNextInMiniPlayer,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Select Variation (5 Types)
        item {
            Column {
                Text(
                    text = "SELECT VARIATION (5 STYLES)",
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
                        val styles = listOf(
                            Triple("Classic Pill", "Floating Pill • Circular progress ring with clean geometry", "graphic_eq"),
                            Triple("Docked Bottom Bar", "Docked Card • Sleek full-width 3dp progress bar line", "equalizer"),
                            Triple("Waveform Rhythm", "Audio Pulse • Live equalizer bars visualizer", "tune"),
                            Triple("Compact Capsule", "Slim Capsule • Space-saving ultra-compact pill", "album"),
                            Triple("Glassmorphic Glow", "Translucent Glass • Frosted surface with subtle border", "palette")
                        )

                        styles.forEachIndexed { index, (styleName, desc, icon) ->
                            if (index > 0) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            val isSelected = selectedMiniPlayerStyle == styleName

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMiniPlayerStyleChanged(styleName) }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onMiniPlayerStyleChanged(styleName) }
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = styleName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    AppIcon(
                                        name = "check",
                                        size = 20.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Control Preferences
        item {
            Column {
                Text(
                    text = "CONTROL PREFERENCES",
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Show Favorite Heart Button",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = showLikeInMiniPlayer,
                                onCheckedChange = { showLikeInMiniPlayer = it }
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Show Next Track Button",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = showNextInMiniPlayer,
                                onCheckedChange = { showNextInMiniPlayer = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
