package com.example.droidlab.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.droidlab.R

/**
 * Reusable Droid Lab Icon component preserving the exact vector line art
 * exported directly from Droid Lab.
 */
@Composable
fun AppIcon(
    name: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp
) {
    val clean = name.lowercase()
        .replace(Regex("^(lucide|ph|msr|mso|mss|ms):"), "")
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')

    val resId: Int? = when (clean) {
        "house", "home" -> R.drawable.ic_ph_house
        "music" -> R.drawable.ic_lucide_music
        "explore", "search", "magnifying_glass" -> R.drawable.ic_ph_magnifying_glass
        "anchor", "keep", "keeps" -> R.drawable.ic_lucide_anchor
        "settings" -> R.drawable.ic_lucide_settings
        "drawer", "menu", "hamburger" -> R.drawable.ic_menu_hamburger
        "more_vert" -> R.drawable.ic_msr_more_vert
        "analyze" -> R.drawable.ic_menu_analyze
        "equalizer" -> R.drawable.ic_menu_equalizer
        "manage" -> R.drawable.ic_lucide_music
        "save" -> R.drawable.ic_lucide_save
        "share" -> R.drawable.ic_lucide_share
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
            "play" -> Icons.Rounded.PlayArrow
            "pause" -> Icons.Rounded.Pause
            "skip_next", "next" -> Icons.Rounded.SkipNext
            "skip_previous", "prev" -> Icons.Rounded.SkipPrevious
            "shuffle" -> Icons.Rounded.Shuffle
            "repeat" -> Icons.Rounded.Repeat
            "heart", "favorite", "like" -> Icons.Rounded.Favorite
            "heart_border", "favorite_border", "unlike" -> Icons.Rounded.FavoriteBorder
            "chevron_down", "expand_more", "down" -> Icons.Rounded.KeyboardArrowDown
            "speaker", "phone_speaker" -> Icons.Rounded.Smartphone
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
            "trending_up" -> Icons.Rounded.TrendingUp
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
