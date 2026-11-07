package com.example.droidlab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon

data class DeviceHardwarePreset(
    val id: String,
    val name: String,
    val category: String,
    val iconName: String,
    val connectionType: String,
    val bands: List<Float>, // 7 bands in dB (-12f to +12f): 60Hz, 150Hz, 400Hz, 1kHz, 2.4kHz, 6kHz, 15kHz
    val bassBoost: Float,
    val virtualizer: Float,
    val description: String
)

val AvailableDevicePresets = listOf(
    DeviceHardwarePreset(
        id = "phone_speaker",
        name = "Phone Speaker",
        category = "Internal",
        iconName = "phone_speaker",
        connectionType = "Built-in Dual Stereo",
        bands = listOf(-2f, -1f, 1f, 3f, 4f, 2f, 1f),
        bassBoost = 0.15f,
        virtualizer = 0.10f,
        description = "Calibrated for smartphone speakers: clear vocals & reduced distortion"
    ),
    DeviceHardwarePreset(
        id = "oneplus_neckband",
        name = "OnePlus Bullets Neckband",
        category = "Neckband",
        iconName = "headset",
        connectionType = "Bluetooth (AAC)",
        bands = listOf(6f, 5f, 2f, -1f, 1f, 3f, 2f),
        bassBoost = 0.70f,
        virtualizer = 0.40f,
        description = "Bass Edition profile: warm acoustic resonance with deep sub-bass"
    ),
    DeviceHardwarePreset(
        id = "oneplus_nord_buds",
        name = "OnePlus Nord Buds 2 Pro",
        category = "Earbuds",
        iconName = "headset",
        connectionType = "Bluetooth (LHDC)",
        bands = listOf(5f, 3f, 0f, 2f, 2f, 4f, 3f),
        bassBoost = 0.60f,
        virtualizer = 0.45f,
        description = "BassWave dynamic tuning: crisp highs and punchy bass kicks"
    ),
    DeviceHardwarePreset(
        id = "samsung_buds2_pro",
        name = "Samsung Galaxy Buds 2 Pro",
        category = "Earbuds",
        iconName = "headset",
        connectionType = "Bluetooth (SSC 24-bit)",
        bands = listOf(3f, 2f, 0f, 1f, 1f, 3f, 4f),
        bassBoost = 0.35f,
        virtualizer = 0.50f,
        description = "Harman target reference curve: balanced mids, clear vocals and airy treble"
    ),
    DeviceHardwarePreset(
        id = "jbl_flip_soundbar",
        name = "JBL Flip 6 / Soundbar",
        category = "Soundbar",
        iconName = "speaker",
        connectionType = "Bluetooth 5.3",
        bands = listOf(5f, 4f, 1f, 0f, 2f, 4f, 3f),
        bassBoost = 0.55f,
        virtualizer = 0.35f,
        description = "JBL Signature Sound: lively, dynamic punch with high-energy presence"
    ),
    DeviceHardwarePreset(
        id = "samsung_home_theater",
        name = "Samsung Home Theater",
        category = "Home Theater",
        iconName = "tv",
        connectionType = "HDMI eARC / Bluetooth",
        bands = listOf(7f, 5f, 2f, 2f, 3f, 5f, 6f),
        bassBoost = 0.80f,
        virtualizer = 0.85f,
        description = "5.1 Surround cinematic profile: room-filling subwoofer and crystal dialogue"
    ),
    DeviceHardwarePreset(
        id = "jbl_home_theater",
        name = "JBL Bar 9.1 Home Theater",
        category = "Home Theater",
        iconName = "speaker",
        connectionType = "Dolby Atmos / eARC",
        bands = listOf(8f, 6f, 3f, 1f, 4f, 6f, 7f),
        bassBoost = 0.85f,
        virtualizer = 0.90f,
        description = "Dolby Atmos immersive profile: explosive low frequencies and 3D acoustics"
    ),
    DeviceHardwarePreset(
        id = "sony_wh1000xm5",
        name = "Sony WH-1000XM5",
        category = "Headphones",
        iconName = "headset",
        connectionType = "Bluetooth (LDAC Hi-Res)",
        bands = listOf(3f, 2f, 1f, 0f, 2f, 3f, 4f),
        bassBoost = 0.40f,
        virtualizer = 0.40f,
        description = "Audiophile grade: studio neutral response with clear micro-details"
    )
)

/**
 * Full Equalizer and Sound Profile Screen:
 * - Detects connected hardware and auto-tunes acoustic response
 * - Supports presets: Phone Speaker, JBL Soundbar, Samsung Galaxy Buds, OnePlus Neckband,
 *   OnePlus Nord Buds 2 Pro, Samsung Home Theater, JBL Home Theater, etc.
 * - Manual multi-band graphic equalizer with interactive curve visualization
 * - Acoustic effects: Bass Boost & 3D Virtualizer
 * - Synchronizes active output device with the Now Playing screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerRoute(
    activeDeviceId: String = "phone_speaker",
    onDeviceSelected: (DeviceHardwarePreset) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val presetMap = remember { AvailableDevicePresets.associateBy { it.id } }
    var currentDeviceId by remember { mutableStateOf(activeDeviceId) }
    var isAutoAdjustEnabled by remember { mutableStateOf(true) }

    val activePreset = presetMap[currentDeviceId] ?: AvailableDevicePresets.first()

    // 7-band EQ state (-12 dB to +12 dB)
    var bands by remember { mutableStateOf(activePreset.bands) }
    var bassBoost by remember { mutableFloatStateOf(activePreset.bassBoost) }
    var virtualizer by remember { mutableFloatStateOf(activePreset.virtualizer) }

    // When active preset changes and auto-adjust is enabled, adjust bands
    LaunchedEffect(currentDeviceId) {
        val p = presetMap[currentDeviceId] ?: AvailableDevicePresets.first()
        if (isAutoAdjustEnabled) {
            bands = p.bands
            bassBoost = p.bassBoost
            virtualizer = p.virtualizer
        }
    }

    val bandLabels = remember { listOf("60Hz", "150Hz", "400Hz", "1kHz", "2.4kHz", "6kHz", "15kHz") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Equalizer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isAutoAdjustEnabled) "Auto-Tuned: ${activePreset.name}" else "Custom Profile",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        AppIcon(name = "arrow_back", size = 22.dp)
                    }
                },
                actions = {
                    // Reset to active hardware preset
                    TextButton(onClick = {
                        bands = activePreset.bands
                        bassBoost = activePreset.bassBoost
                        virtualizer = activePreset.virtualizer
                        isAutoAdjustEnabled = true
                    }) {
                        Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Connected Hardware Profile & Auto-Tune Toggle
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                AppIcon(
                                    name = activePreset.iconName,
                                    size = 24.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activePreset.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${activePreset.connectionType} • Active Output",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Connection status indicator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = activePreset.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 0.8.dp
                        )

                        Spacer(Modifier.height(12.dp))

                        // Auto-adjust Toggle Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Tune for Hardware",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Auto-calibrates curve based on connected device acoustics",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = isAutoAdjustEnabled,
                                onCheckedChange = { checked ->
                                    isAutoAdjustEnabled = checked
                                    if (checked) {
                                        bands = activePreset.bands
                                        bassBoost = activePreset.bassBoost
                                        virtualizer = activePreset.virtualizer
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Section 2: Device Hardware Presets
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hardware Device Presets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to switch output",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        items(AvailableDevicePresets, key = { it.id }) { preset ->
                            val isSelected = preset.id == currentDeviceId

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    }
                                ),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 0.8.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .width(175.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (isSelected && preset.id != "phone_speaker") {
                                            // Deselect and revert to phone speaker
                                            val fallback = AvailableDevicePresets.first { it.id == "phone_speaker" }
                                            currentDeviceId = fallback.id
                                            onDeviceSelected(fallback)
                                        } else {
                                            currentDeviceId = preset.id
                                            onDeviceSelected(preset)
                                        }
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(
                                                name = preset.iconName,
                                                size = 18.dp,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }

                                        if (isSelected) {
                                            AppIcon(
                                                name = "check",
                                                size = 18.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    Text(
                                        text = preset.name,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(Modifier.height(2.dp))

                                    Text(
                                        text = preset.category,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Visual Spline Curve Canvas
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Frequency Response Curve",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "-12 dB to +12 dB",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Dynamic Smooth Spline Curve linking all 7 EQ points
                        val primaryColor = MaterialTheme.colorScheme.primary
                        val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            val w = size.width
                            val h = size.height
                            val midY = h / 2f

                            // Draw reference grid lines: +12dB (top), 0dB (mid), -12dB (bottom)
                            drawLine(color = gridColor, start = Offset(0f, 0f), end = Offset(w, 0f), strokeWidth = 1f)
                            drawLine(color = gridColor.copy(alpha = 0.4f), start = Offset(0f, midY), end = Offset(w, midY), strokeWidth = 1.2f)
                            drawLine(color = gridColor, start = Offset(0f, h), end = Offset(w, h), strokeWidth = 1f)

                            // Calculate 7 point coordinates
                            val stepX = w / (bands.size - 1)
                            val points = bands.mapIndexed { index, db ->
                                val x = index * stepX
                                // Map -12dB .. +12dB to height..0
                                val normalized = ((db.coerceIn(-12f, 12f) + 12f) / 24f)
                                val y = h * (1f - normalized)
                                Offset(x, y)
                            }

                            // Spline Path
                            val curvePath = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 0 until points.size - 1) {
                                    val p0 = points[i]
                                    val p1 = points[i + 1]
                                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                                    val controlY1 = p0.y
                                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                                    val controlY2 = p1.y
                                    cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                                }
                            }

                            // Shaded area beneath the curve
                            val fillPath = Path().apply {
                                addPath(curvePath)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                    startY = 0f,
                                    endY = h
                                )
                            )

                            // Main stroke curve
                            drawPath(
                                path = curvePath,
                                color = primaryColor,
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Control points
                            points.forEach { pt ->
                                drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = pt)
                                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pt)
                            }
                        }
                    }
                }
            }

            // Section 4: 7-Band Graphic Equalizer Sliders (User can modify manually)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Manual Band Adjustment",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // 7 Vertical Slider Columns
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bands.forEachIndexed { index, dbValue ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // dB label
                                    val formattedDb = if (dbValue > 0) "+${dbValue.toInt()}" else "${dbValue.toInt()}"
                                    Text(
                                        text = formattedDb,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dbValue != 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    // Vertical Slider Bar (Interactive Drag)
                                    Box(
                                        modifier = Modifier
                                            .width(26.dp)
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(13.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                            .pointerInput(index) {
                                                detectDragGestures { change, _ ->
                                                    change.consume()
                                                    val touchY = change.position.y.coerceIn(0f, 130.dp.toPx())
                                                    val fraction = 1f - (touchY / 130.dp.toPx())
                                                    val newDb = (-12f + fraction * 24f).coerceIn(-12f, 12f)
                                                    val newBands = bands.toMutableList()
                                                    newBands[index] = (newDb * 2).toInt() / 2f
                                                    bands = newBands
                                                    isAutoAdjustEnabled = false
                                                }
                                            },
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        // Level Fill
                                        val fillFraction = ((dbValue + 12f) / 24f).coerceIn(0.05f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(fillFraction)
                                                .clip(RoundedCornerShape(13.dp))
                                                .background(MaterialTheme.colorScheme.primary)
                                        )

                                        // Thumb Indicator Handle
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Frequency name
                                    Text(
                                        text = bandLabels[index],
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 5: Acoustic Effects (Bass Boost & Virtualizer)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(0.85.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Acoustic Tuning & Enhancement",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Bass Boost
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Bass Boost", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                                Text(text = "${(bassBoost * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Slider(
                                value = bassBoost,
                                onValueChange = {
                                    bassBoost = it
                                    isAutoAdjustEnabled = false
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        // 3D Virtualizer
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "3D Virtualizer / Surround", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                                Text(text = "${(virtualizer * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Slider(
                                value = virtualizer,
                                onValueChange = {
                                    virtualizer = it
                                    isAutoAdjustEnabled = false
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
