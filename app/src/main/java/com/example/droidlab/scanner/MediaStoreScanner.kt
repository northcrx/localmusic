package com.example.droidlab.scanner

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
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
    val isCompleted: Boolean = false
)

class MediaStoreScanner(
    private val context: Context,
    private val songDao: SongDao
) {

    private val _scanState = MutableStateFlow(ScanProgress())
    val scanState: StateFlow<ScanProgress> = _scanState.asStateFlow()

    suspend fun scanMusic(allowedRelativePaths: Set<String>? = null): Int = withContext(Dispatchers.IO) {
        _scanState.value = ScanProgress(isScanning = true, progress = 0f, currentFileName = "Starting scan...")

        // 1. Get existing IDs & modified timestamps from Room for incremental scan
        val existingMap = songDao.getAllExistingIdsAndModified().associate { it.mediaStoreId to it.dateModified }
        val activeMediaStoreIds = mutableListOf<Long>()
        val songsToUpsert = mutableListOf<SongEntity>()

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
            MediaStore.Audio.Media.YEAR
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection.add("bitrate")
            projection.add("sample_rate")
        }
        // Always query DATA as fallback path if needed
        projection.add(MediaStore.Audio.Media.DATA)

        // Filter: ONLY music (no ringtones, alarms, notifications)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                uri,
                projection.toTypedArray(),
                selection,
                null,
                sortOrder
            )

            if (cursor != null) {
                val totalCount = cursor.count
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
                val bitrateCol = cursor.getColumnIndex("bitrate")
                val sampleRateCol = cursor.getColumnIndex("sample_rate")

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
                    if (allowedRelativePaths != null && allowedRelativePaths.isNotEmpty()) {
                        val matches = allowedRelativePaths.any { path ->
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
                        val sampleRate = if (sampleRateCol >= 0) cursor.getInt(sampleRateCol) else 0

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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        // 2. Batch upsert newly discovered or modified songs into Room
        if (songsToUpsert.isNotEmpty()) {
            songDao.upsertSongs(songsToUpsert)
        }

        // 3. Remove songs from Room that were deleted from device storage
        if (activeMediaStoreIds.isNotEmpty()) {
            songDao.deleteSongsNotIn(activeMediaStoreIds)
        } else if (allowedRelativePaths == null) {
            // No songs found at all on device
            songDao.clearAll()
        }

        _scanState.value = ScanProgress(
            isScanning = false,
            progress = 1f,
            currentFileName = "Scan Complete",
            totalFound = activeMediaStoreIds.size,
            isCompleted = true
        )

        activeMediaStoreIds.size
    }
}
