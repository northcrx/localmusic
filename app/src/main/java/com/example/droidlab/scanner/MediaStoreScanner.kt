package com.example.droidlab.scanner

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.droidlab.data.dao.SongDao
import com.example.droidlab.data.entity.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

data class ScanProgress(
    val isScanning: Boolean = false,
    val progress: Float = 0f,
    val currentFileName: String = "",
    val totalFound: Int = 0,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

class MediaStoreScanner(
    private val context: Context,
    private val songDao: SongDao
) {

    private val _scanState = MutableStateFlow(ScanProgress())
    val scanState: StateFlow<ScanProgress> = _scanState.asStateFlow()

    suspend fun scanMusic(allowedRelativePaths: Set<String>? = null): Int = withContext(Dispatchers.IO) {
        // 1. Verify Audio/Storage Permission before querying MediaStore
        val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, requiredPermission) != PackageManager.PERMISSION_GRANTED) {
            val error = "Audio permission ($requiredPermission) is not granted. Cannot scan device music."
            Log.e("MediaStoreScanner", error)
            _scanState.value = ScanProgress(
                isScanning = false,
                progress = 0f,
                currentFileName = "",
                totalFound = 0,
                isCompleted = true,
                errorMessage = error
            )
            return@withContext 0
        }

        _scanState.value = ScanProgress(isScanning = true, progress = 0f, currentFileName = "Starting scan...")

        // 2. Ignore placeholder folder filter (scan all music until real folders are connected)
        val placeholderNames = setOf("Music", "Download", "Podcasts", "Audiobooks", "/storage/emulated/0/Music")
        val isPlaceholderFilter = allowedRelativePaths == null || allowedRelativePaths.isEmpty() ||
            allowedRelativePaths.all { it in placeholderNames }
        val effectiveAllowedPaths = if (isPlaceholderFilter) null else allowedRelativePaths

        // 3. Get existing IDs & modified timestamps from Room for incremental scan
        val existingMap = try {
            songDao.getAllExistingIdsAndModified().associate { it.mediaStoreId to it.dateModified }
        } catch (e: Exception) {
            Log.e("MediaStoreScanner", "Failed to read existing songs from Room", e)
            emptyMap()
        }

        val activeMediaStoreIds = mutableListOf<Long>()
        val songsToUpsert = mutableListOf<SongEntity>()

        // 4. Safe projection: "sample_rate" is NOT a valid MediaStore column; "bitrate" only on API 30+
        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATA
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection.add(MediaStore.Audio.Media.BITRATE)
        }

        // 5. Robust Selection: Include IS_MUSIC != 0 OR duration >= 60s, exclude ringtones/alarms/notifications
        val selection = "(${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.DURATION} >= 60000) " +
            "AND ${MediaStore.Audio.Media.IS_RINGTONE} == 0 " +
            "AND ${MediaStore.Audio.Media.IS_ALARM} == 0 " +
            "AND ${MediaStore.Audio.Media.IS_NOTIFICATION} == 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var cursor: Cursor? = null
        var querySucceeded = false

        try {
            cursor = context.contentResolver.query(
                uri,
                projection.toTypedArray(),
                selection,
                null,
                sortOrder
            )

            if (cursor == null) {
                val error = "MediaStore query returned a null cursor."
                Log.e("MediaStoreScanner", error)
                _scanState.value = ScanProgress(
                    isScanning = false,
                    progress = 0f,
                    currentFileName = "",
                    totalFound = 0,
                    isCompleted = true,
                    errorMessage = error
                )
                return@withContext 0
            }

            val totalCount = cursor.count
            Log.d("MediaStoreScanner", "MediaStore returned $totalCount total audio rows matching selection")
            var processed = 0

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val mimeCol = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
            val dateAddedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
            val sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val trackCol = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
            val yearCol = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val relPathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
            } else -1
            val bitrateCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getColumnIndex(MediaStore.Audio.Media.BITRATE)
            } else -1

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                activeMediaStoreIds.add(id)

                val dateModified = if (dateModifiedCol >= 0) cursor.getLong(dateModifiedCol) else 0L

                // Resolve relative path
                var relativePath = ""
                if (relPathCol >= 0) {
                    relativePath = cursor.getString(relPathCol) ?: ""
                }
                if (relativePath.isBlank() && dataCol >= 0) {
                    val fullPath = cursor.getString(dataCol) ?: ""
                    val parent = java.io.File(fullPath).parent
                    if (parent != null) {
                        relativePath = parent
                    }
                }

                // Filter by allowed paths if specified
                if (effectiveAllowedPaths != null) {
                    val matches = effectiveAllowedPaths.any { path ->
                        relativePath.contains(path, ignoreCase = true)
                    }
                    if (!matches) {
                        processed++
                        continue
                    }
                }

                // Incremental check: only re-index if file is new or modified
                val lastModifiedInDb = existingMap[id]
                if (lastModifiedInDb == null || lastModifiedInDb != dateModified) {
                    val title = (if (titleCol >= 0) cursor.getString(titleCol) else null)?.takeIf { it.isNotBlank() }
                        ?: "Unknown Track"
                    val artist = (if (artistCol >= 0) cursor.getString(artistCol) else null)?.takeIf { it.isNotBlank() && it != "<unknown>" }
                        ?: "Unknown Artist"
                    val album = (if (albumCol >= 0) cursor.getString(albumCol) else null)?.takeIf { it.isNotBlank() }
                        ?: "Unknown Album"
                    val durationMs = if (durationCol >= 0) cursor.getLong(durationCol) else 0L
                    val mimeType = if (mimeCol >= 0) cursor.getString(mimeCol) ?: "audio/*" else "audio/*"
                    val dateAdded = if (dateAddedCol >= 0) cursor.getLong(dateAddedCol) else 0L
                    val sizeBytes = if (sizeCol >= 0) cursor.getLong(sizeCol) else 0L
                    val albumId = if (albumIdCol >= 0) cursor.getLong(albumIdCol) else 0L
                    val artistId = if (artistIdCol >= 0) cursor.getLong(artistIdCol) else 0L
                    val track = if (trackCol >= 0) cursor.getInt(trackCol) else 0
                    val year = if (yearCol >= 0) cursor.getInt(yearCol) else 0
                    val bitrate = if (bitrateCol >= 0) cursor.getInt(bitrateCol) else 0
                    val sampleRate = 0 // Resolved later during playback

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    val songEntity = SongEntity(
                        mediaStoreId = id,
                        title = title,
                        artist = artist,
                        album = album,
                        durationMs = durationMs,
                        contentUriString = contentUri.toString(),
                        relativePath = relativePath,
                        mimeType = mimeType,
                        trackNumber = track,
                        year = year,
                        bitrate = bitrate,
                        sampleRate = sampleRate,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        sizeBytes = sizeBytes,
                        albumId = albumId,
                        artistId = artistId
                    )
                    songsToUpsert.add(songEntity)
                }

                processed++

                // Throttle UI progress updates (every 50 items or on completion)
                val updateThreshold = if (totalCount < 50) 1 else 50
                if (processed % updateThreshold == 0 || processed == totalCount) {
                    val progressFraction = if (totalCount > 0) processed.toFloat() / totalCount else 1f
                    val currentName = if (titleCol >= 0) cursor.getString(titleCol) ?: "Audio file" else "Audio file"
                    _scanState.value = ScanProgress(
                        isScanning = true,
                        progress = progressFraction,
                        currentFileName = currentName,
                        totalFound = activeMediaStoreIds.size,
                        isCompleted = false
                    )
                }
            }

            querySucceeded = true
        } catch (e: Exception) {
            val error = "Error querying MediaStore: ${e.message}"
            Log.e("MediaStoreScanner", error, e)
            _scanState.value = ScanProgress(
                isScanning = false,
                progress = 0f,
                currentFileName = "",
                totalFound = 0,
                isCompleted = true,
                errorMessage = error
            )
            return@withContext 0
        } finally {
            cursor?.close()
        }

        // 6. Safe database operations only after successful query
        if (querySucceeded) {
            if (songsToUpsert.isNotEmpty()) {
                songDao.upsertSongs(songsToUpsert)
            }
            Log.d("MediaStoreScanner", "Saved ${songsToUpsert.size} new/modified songs to Room. Total active songs: ${activeMediaStoreIds.size}")

            // Remove songs from Room that were deleted from device storage
            if (activeMediaStoreIds.isNotEmpty() && effectiveAllowedPaths == null) {
                songDao.deleteSongsNotIn(activeMediaStoreIds)
            }
        }

        _scanState.value = ScanProgress(
            isScanning = false,
            progress = 1f,
            currentFileName = "Scan Complete",
            totalFound = activeMediaStoreIds.size,
            isCompleted = true,
            errorMessage = null
        )

        activeMediaStoreIds.size
    }
}
