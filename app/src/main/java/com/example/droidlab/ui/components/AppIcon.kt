package com.example.droidlab.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Universal Icon Component supporting local drawables and fallback Material Icons.
 */
@Composable
fun AppIcon(
    name: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    val context = LocalContext.current
    val clean = name.lowercase().trim()

    val resId = when (clean) {
        "home" -> context.resources.getIdentifier("ic_home", "drawable", context.packageName).takeIf { it != 0 }
        "search" -> context.resources.getIdentifier("ic_search", "drawable", context.packageName).takeIf { it != 0 }
        "keep" -> context.resources.getIdentifier("ic_keep", "drawable", context.packageName).takeIf { it != 0 }
        "music" -> context.resources.getIdentifier("ic_music", "drawable", context.packageName).takeIf { it != 0 }
        "settings" -> context.resources.getIdentifier("ic_settings", "drawable", context.packageName).takeIf { it != 0 }
        "more_vert", "three_dots" -> context.resources.getIdentifier("ic_three_dots", "drawable", context.packageName).takeIf { it != 0 }
        "explore" -> context.resources.getIdentifier("ic_explore", "drawable", context.packageName).takeIf { it != 0 }
        else -> null
    }

    if (resId != null) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier.size(size),
            tint = tint
        )
    } else {
        val vectorIcon: ImageVector = when (clean) {
            "close", "clear" -> Icons.Rounded.Close
            "arrow_back", "back" -> Icons.AutoMirrored.Rounded.ArrowBack
            "tune", "filter" -> Icons.Rounded.Tune
            "lightbulb" -> Icons.Rounded.Lightbulb
            "tips_and_updates" -> Icons.Rounded.TipsAndUpdates
            "check" -> Icons.Rounded.Check
            "check_circle" -> Icons.Rounded.CheckCircle
            "headset", "headphones" -> Icons.Rounded.Headphones
            "bluetooth" -> Icons.Rounded.Bluetooth
            "equalizer", "eq" -> Icons.Rounded.Equalizer
            "graphic_eq" -> Icons.Rounded.GraphicEq
            "play" -> Icons.Rounded.PlayArrow
            "pause" -> Icons.Rounded.Pause
            "skip_next", "next" -> Icons.Rounded.SkipNext
            "skip_previous", "prev" -> Icons.Rounded.SkipPrevious
            "shuffle" -> Icons.Rounded.Shuffle
            "repeat" -> Icons.Rounded.Repeat
            "heart", "favorite", "like" -> Icons.Rounded.Favorite
            "heart_border", "favorite_border", "unlike" -> Icons.Rounded.FavoriteBorder
            "chevron_down", "expand_more", "down" -> Icons.Rounded.KeyboardArrowDown
            "speaker" -> Icons.Rounded.Speaker
            "phone_speaker" -> Icons.Rounded.Smartphone
            "tv", "home_theater" -> Icons.Rounded.Tv
            "lyrics" -> Icons.AutoMirrored.Rounded.Notes
            "queue", "up_next" -> Icons.AutoMirrored.Rounded.QueueMusic
            "timer", "sleep_timer", "schedule" -> Icons.Rounded.Timer
            "bedtime" -> Icons.Rounded.Bedtime
            "person" -> Icons.Rounded.Person
            "person_add" -> Icons.Rounded.PersonAdd
            "delete" -> Icons.Rounded.Delete
            "download" -> Icons.Rounded.Download
            "cloud_download" -> Icons.Rounded.CloudDownload
            "palette" -> Icons.Rounded.Palette
            "font", "text_fields" -> Icons.Rounded.TextFields
            "wifi_off" -> Icons.Rounded.WifiOff
            "refresh" -> Icons.Rounded.Refresh
            "star" -> Icons.Rounded.Star
            "speed" -> Icons.Rounded.Speed
            "history" -> Icons.Rounded.History
            "trending_up" -> Icons.AutoMirrored.Rounded.TrendingUp
            "public" -> Icons.Rounded.Public
            "library_music" -> Icons.Rounded.LibraryMusic
            else -> Icons.Rounded.Circle
        }

        Icon(
            imageVector = vectorIcon,
            contentDescription = contentDescription,
            modifier = modifier.size(size),
            tint = tint
        )
    }
}
