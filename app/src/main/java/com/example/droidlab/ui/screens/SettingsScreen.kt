package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon

/**
 * Settings Screen with Appearance subscreen supporting font switching:
 * App Font (Outfit) vs System Default Font.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit
) {
    var isAppearanceOpen by remember { mutableStateOf(false) }

    // User preference states
    var selectedFont by remember { mutableStateOf("App Font (Outfit)") }
    var selectedThemeMode by remember { mutableStateOf("Dark Theme") }
    var selectedAccent by remember { mutableStateOf("Electric Indigo") }

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
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(name = "text_fields", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Typography & Font Family",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Select your preferred rendering font",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Option 1: App Font (Outfit)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedFont = "App Font (Outfit)" }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFont == "App Font (Outfit)",
                                    onClick = { selectedFont = "App Font (Outfit)" }
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "App Font (Outfit)",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Modern geometric Droid Lab typeface designed for music apps",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Option 2: System Font
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedFont = "System Font (Default)" }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFont == "System Font (Default)",
                                    onClick = { selectedFont = "System Font (Default)" }
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "System Font (Default)",
                                        fontSize = 14.5.sp,
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Native Android system font (Roboto) matching device global settings",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Default,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Theme Mode Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(name = "palette", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Theme Mode",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            listOf("Dark Theme (Recommended)", "Light Theme", "Follow System").forEach { mode ->
                                val isSelected = selectedThemeMode.startsWith(mode.take(4))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedThemeMode = mode }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedThemeMode = mode }
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(text = mode, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // Accent Color Swatches
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Accent Palette",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))

                            val accents = listOf(
                                Pair("Electric Indigo", Color(0xFF4F80FF)),
                                Pair("Violet Dusk", Color(0xFF8B5CF6)),
                                Pair("Emerald Wave", Color(0xFF10B981)),
                                Pair("Sunset Coral", Color(0xFFF59E0B))
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                accents.forEach { (name, color) ->
                                    val isSelected = selectedAccent == name
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedAccent = name }
                                            .padding(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(color),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                AppIcon(name = "check", size = 20.dp, tint = Color.White)
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Text(text = name.split(" ").first(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Main Settings Screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section: App Preferences
                item {
                    Text(
                        text = "Preferences",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Column {
                            ListItem(
                                headlineContent = { Text("App Preferences", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Notifications, haptic feedback and privacy") },
                                leadingContent = { AppIcon(name = "settings", size = 22.dp) },
                                modifier = Modifier.clickable { }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // Appearance Setting item (tapping opens Appearance subscreen)
                            ListItem(
                                headlineContent = { Text("Appearance", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Font choice ($selectedFont), theme mode and colors") },
                                leadingContent = { AppIcon(name = "palette", size = 22.dp, tint = MaterialTheme.colorScheme.primary) },
                                trailingContent = {
                                    AppIcon(name = "skip_next", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                modifier = Modifier.clickable { isAppearanceOpen = true }
                            )
                        }
                    }
                }

                // Section: Audio & Playback
                item {
                    Text(
                        text = "Audio & Engine",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Column {
                            ListItem(
                                headlineContent = { Text("Equalizer & Sound Effects", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Presets, Bass Boost and Virtualizer") },
                                leadingContent = { AppIcon(name = "equalizer", size = 22.dp) },
                                modifier = Modifier.clickable { }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            ListItem(
                                headlineContent = { Text("Storage & Cache", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Download directories and album art cache") },
                                leadingContent = { AppIcon(name = "cloud_download", size = 22.dp) },
                                modifier = Modifier.clickable { }
                            )
                        }
                    }
                }

                // Section: About
                item {
                    Text(
                        text = "About",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "LocalMusic Prototype v1.0",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "A modern UI/UX design architecture prototype built with Jetpack Compose & Material 3 Expressive. Media3 playback engine integration planned for future iterations.",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
