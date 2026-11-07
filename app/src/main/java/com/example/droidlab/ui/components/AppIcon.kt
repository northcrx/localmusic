package com.example.droidlab.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.Sort
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
 * Universal Icon Component supporting local drawables and Material Rounded icons fallback.
 */
@Composable
fun AppIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val clean = name.lowercase().trim()

    // 1. Try resolving exact drawable resources if present
    val resId = when (clean) {
        "home" -> context.resources.getIdentifier("ic_ph_house", "drawable", context.packageName).takeIf { it != 0 }
            ?: context.resources.getIdentifier("ic_home", "drawable", context.packageName).takeIf { it != 0 }
        "search", "explore" -> context.resources.getIdentifier("ic_ph_magnifying_glass", "drawable", context.packageName).takeIf { it != 0 }
            ?: context.resources.getIdentifier("ic_msr_search", "drawable", context.packageName).takeIf { it != 0 }
        "music" -> context.resources.getIdentifier("ic_lucide_music", "drawable", context.packageName).takeIf { it != 0 }
        "keep", "download", "downloads" -> context.resources.getIdentifier("ic_msr_download", "drawable", context.packageName).takeIf { it != 0 }
        "playlist", "playlists" -> context.resources.getIdentifier("ic_lucide_library", "drawable", context.packageName).takeIf { it != 0 }
        "settings" -> context.resources.getIdentifier("ic_lucide_settings", "drawable", context.packageName).takeIf { it != 0 }
        "more_vert", "three_dots" -> context.resources.getIdentifier("ic_msr_more_vert", "drawable", context.packageName).takeIf { it != 0 }
        "drawer", "menu", "hamburger" -> context.resources.getIdentifier("ic_menu_hamburger", "drawable", context.packageName).takeIf { it != 0 }
        "equalizer", "eq" -> context.resources.getIdentifier("ic_menu_equalizer", "drawable", context.packageName).takeIf { it != 0 }
        "delete" -> context.resources.getIdentifier("ic_msr_delete", "drawable", context.packageName).takeIf { it != 0 }
        "palette" -> context.resources.getIdentifier("ic_msr_palette", "drawable", context.packageName).takeIf { it != 0 }
        "person" -> context.resources.getIdentifier("ic_msr_person", "drawable", context.packageName).takeIf { it != 0 }
        else -> null
    }

    if (resId != null) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = contentDescription ?: name,
            modifier = modifier.size(size),
            tint = tint
        )
    } else {
        // Complete vector icon mapping - every icon has a rich Material icon fallback
        val vectorIcon: ImageVector = when (clean) {
            "home" -> Icons.Rounded.Home
            "search", "explore" -> Icons.Rounded.Search
            "music", "song" -> Icons.Rounded.MusicNote
            "playlist", "playlists", "playlist_play" -> Icons.AutoMirrored.Rounded.PlaylistPlay
            "playlist_add" -> Icons.AutoMirrored.Rounded.PlaylistAdd
            "keep", "download", "downloads" -> Icons.Rounded.Download
            "settings" -> Icons.Rounded.Settings
            "more_vert", "three_dots" -> Icons.Rounded.MoreVert
            "more_horiz", "more_horizontal", "dots" -> Icons.Rounded.MoreHoriz
            "drawer", "menu", "hamburger" -> Icons.Rounded.Menu
            "close", "clear" -> Icons.Rounded.Close
            "arrow_back", "back", "chevron_left" -> Icons.AutoMirrored.Rounded.ArrowBack
            "arrow_forward", "forward", "arrow_right" -> Icons.AutoMirrored.Rounded.ArrowForward
            "chevron_right", "forward_chevron" -> Icons.Rounded.ChevronRight
            "tune", "filter" -> Icons.Rounded.Tune
            "lightbulb" -> Icons.Rounded.Lightbulb
            "tips_and_updates" -> Icons.Rounded.TipsAndUpdates
            "check" -> Icons.Rounded.Check
            "check_circle" -> Icons.Rounded.CheckCircle
            "headset", "headphones", "earbuds", "neckband" -> Icons.Rounded.Headphones
            "bluetooth" -> Icons.Rounded.Bluetooth
            "equalizer", "eq" -> Icons.Rounded.Equalizer
            "graphic_eq" -> Icons.Rounded.GraphicEq
            "play" -> Icons.Rounded.PlayArrow
            "pause" -> Icons.Rounded.Pause
            "skip_next", "next" -> Icons.Rounded.SkipNext
            "skip_previous", "previous" -> Icons.Rounded.SkipPrevious
            "shuffle" -> Icons.Rounded.Shuffle
            "repeat" -> Icons.Rounded.Repeat
            "heart", "favorite", "like" -> Icons.Rounded.Favorite
            "heart_border", "favorite_border", "unlike" -> Icons.Rounded.FavoriteBorder
            "chevron_down", "expand_more", "down" -> Icons.Rounded.KeyboardArrowDown
            "speaker", "soundbar" -> Icons.Rounded.Speaker
            "phone_speaker", "phone", "smartphone" -> Icons.Rounded.Smartphone
            "tv", "home_theater", "theater" -> Icons.Rounded.Tv
            "lyrics" -> Icons.AutoMirrored.Rounded.Notes
            "queue", "up_next" -> Icons.AutoMirrored.Rounded.QueueMusic
            "drag_handle", "reorder", "handle" -> Icons.Rounded.DragHandle
            "timer", "sleep_timer", "schedule" -> Icons.Rounded.Timer
            "bedtime", "night", "moon", "sleep" -> Icons.Rounded.Bedtime
            "storage", "device", "hard_drive" -> Icons.Rounded.Storage
            "album", "disc" -> Icons.Rounded.Album
            "refresh" -> Icons.Rounded.Refresh
            "person" -> Icons.Rounded.Person
            "person_add" -> Icons.Rounded.PersonAdd
            "delete" -> Icons.Rounded.Delete
            "cloud_download" -> Icons.Rounded.CloudDownload
            "palette" -> Icons.Rounded.Palette
            "font", "text_fields" -> Icons.Rounded.TextFields
            "sort" -> Icons.AutoMirrored.Rounded.Sort
            "share" -> Icons.Rounded.Share
            "info", "file_details", "description" -> Icons.Rounded.Info
            "star" -> Icons.Rounded.Star
            "history" -> Icons.Rounded.History
            "trending_up" -> Icons.AutoMirrored.Rounded.TrendingUp
            "public" -> Icons.Rounded.Public
            "library_music" -> Icons.Rounded.LibraryMusic
            // Hardware preset name fallbacks
            else -> when {
                clean.contains("phone") || clean.contains("speaker") && clean.contains("phone") -> Icons.Rounded.Smartphone
                clean.contains("neckband") || clean.contains("buds") || clean.contains("headphone") || clean.contains("sony") -> Icons.Rounded.Headphones
                clean.contains("theater") || clean.contains("tv") -> Icons.Rounded.Tv
                clean.contains("speaker") || clean.contains("soundbar") || clean.contains("jbl") -> Icons.Rounded.Speaker
                clean.contains("music") || clean.contains("song") -> Icons.Rounded.MusicNote
                else -> Icons.Rounded.MusicNote
            }
        }

        Icon(
            imageVector = vectorIcon,
            contentDescription = contentDescription ?: name,
            modifier = modifier.size(size),
            tint = tint
        )
    }
}
