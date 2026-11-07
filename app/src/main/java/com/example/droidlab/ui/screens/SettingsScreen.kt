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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon
import com.example.droidlab.ui.theme.ManropeFontFamily
import com.example.droidlab.ui.theme.SpaceGroteskFontFamily

/**
 * Settings Screen with Appearance subscreen supporting dynamic font selection:
 * - Manrope (Default Droid Lab App Font)
 * - Space Grotesk (Modern geometric alternative)
 * - System Font (Android system default)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    selectedFontPreference: String = "Manrope",
    onFontPreferenceChanged: (String) -> Unit = {}
) {
    var isAppearanceOpen by remember { mutableStateOf(false) }

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

                            // Font Options list
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section: App & Appearance
                item {
                    Text(
                        text = "App & Interface",
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
                                supportingContent = { Text("App Font ($selectedFontPreference)") },
                                leadingContent = { AppIcon(name = "palette", size = 22.dp, tint = MaterialTheme.colorScheme.primary) },
                                trailingContent = {
                                    AppIcon(name = "arrow_forward", size = 20.dp, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                headlineContent = { Text("Audio Quality", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Lossless FLAC 24-bit / 96kHz") },
                                leadingContent = { AppIcon(name = "graphic_eq", size = 22.dp) }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            ListItem(
                                headlineContent = { Text("Hardware Equalizer", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Auto-profiles based on audio output device") },
                                leadingContent = { AppIcon(name = "equalizer", size = 22.dp) }
                            )
                        }
                    }
                }

                // Section: Downloads & Storage
                item {
                    Text(
                        text = "Storage & Cache",
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
                                headlineContent = { Text("Download Directory", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Internal Storage / Music / DroidLab") },
                                leadingContent = { AppIcon(name = "download", size = 22.dp) }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            ListItem(
                                headlineContent = { Text("Clear Cache", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Free up 142.5 MB cached artwork and audio") },
                                leadingContent = { AppIcon(name = "delete", size = 22.dp) }
                            )
                        }
                    }
                }

                // App Info & Version
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Droid Lab Local Music",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "Version 1.0.0 • Build 2026.11",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
