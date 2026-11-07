package com.example.droidlab.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.droidlab.data.dao.PlaybackStateDao
import com.example.droidlab.data.dao.SongDao
import com.example.droidlab.data.entity.PlaybackStateEntity
import com.example.droidlab.data.entity.SongEntity
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerController(
    private val context: Context,
    private val songDao: SongDao,
    private val playbackStateDao: PlaybackStateDao
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var controller: MediaController? = null

    // StateFlows exposed to UI
    private val _currentSong = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<SongEntity>>(emptyList())
    val queue: StateFlow<List<SongEntity>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _currentFormatDescription = MutableStateFlow("On device")
    val currentFormatDescription: StateFlow<String> = _currentFormatDescription.asStateFlow()

    // One-time playback error events for UI toasts/snackbars
    private val _playbackErrorEvent = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val playbackErrorEvent: SharedFlow<String> = _playbackErrorEvent.asSharedFlow()

    private var positionTickerJob: Job? = null
    private var isRestoringSavedState = false

    init {
        initMediaController()
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                val mediaController = controllerFuture.get()
                controller = mediaController
                attachControllerListener(mediaController)
                syncStateFromController(mediaController)

                // Restore saved queue and playback state from Room
                scope.launch {
                    restorePlaybackState()
                }
            } catch (e: Exception) {
                Log.e("PlayerController", "Error initializing MediaController", e)
            }
        }, MoreExecutors.directExecutor())
    }

    private fun attachControllerListener(player: MediaController) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("PlayerController", "onIsPlayingChanged: isPlaying=$isPlaying")
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startPositionTicker()
                } else {
                    stopPositionTicker()
                    // Save playback position to Room only on pause
                    savePlaybackState()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSongFromMediaItem(mediaItem)
                updateDuration()
                updateFormatDescription()
                // Save state on song change
                savePlaybackState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updateDuration()
                updateFormatDescription()
                if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    stopPositionTicker()
                    savePlaybackState()
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleEnabled.value = shuffleModeEnabled
                savePlaybackState()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
                savePlaybackState()
            }

            override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                syncQueueWithController()
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerController", "Playback error: ${error.message}", error)
                if (player.hasNextMediaItem()) {
                    player.seekToNextMediaItem()
                    player.prepare()
                    player.play()
                    _playbackErrorEvent.tryEmit("Can't play file, skipped to next track")
                } else {
                    player.pause()
                    _isPlaying.value = false
                    _playbackErrorEvent.tryEmit("Can't play this file")
                }
            }
        })
    }

    private fun syncStateFromController(player: MediaController) {
        _isPlaying.value = player.isPlaying
        _shuffleEnabled.value = player.shuffleModeEnabled
        _repeatMode.value = player.repeatMode
        updateCurrentSongFromMediaItem(player.currentMediaItem)
        updateDuration()
        updateFormatDescription()
        syncQueueWithController()
        if (player.isPlaying) {
            startPositionTicker()
        }
    }

    private fun updateCurrentSongFromMediaItem(mediaItem: MediaItem?) {
        val mediaId = mediaItem?.mediaId?.toLongOrNull()
        if (mediaId == null) {
            _currentSong.value = null
            return
        }
        val currentQueue = _queue.value
        val index = currentQueue.indexOfFirst { it.mediaStoreId == mediaId }
        if (index >= 0) {
            _currentIndex.value = index
            _currentSong.value = currentQueue[index]
        } else {
            scope.launch(Dispatchers.IO) {
                val song = songDao.getSongById(mediaId)
                withContext(Dispatchers.Main) {
                    _currentSong.value = song
                }
            }
        }
    }

    private fun updateDuration() {
        val d = controller?.duration ?: 0L
        _durationMs.value = if (d > 0L) d else (_currentSong.value?.durationMs ?: 1L).coerceAtLeast(1L)
        _positionMs.value = controller?.currentPosition ?: 0L
    }

    private fun updateFormatDescription() {
        val song = _currentSong.value
        val tracks = controller?.currentTracks
        var audioFormat: Format? = null
        if (tracks != null) {
            for (group in tracks.groups) {
                for (i in 0 until group.length) {
                    val f = group.getTrackFormat(i)
                    if (f.sampleMimeType?.startsWith("audio") == true) {
                        audioFormat = f
                        break
                    }
                }
                if (audioFormat != null) break
            }
        }

        val desc = when {
            audioFormat != null -> formatFromAudioFormat(audioFormat)
            song != null -> {
                val ext = song.mimeType.substringAfterLast('/').uppercase()
                val khz = if (song.sampleRate > 0) " • ${song.sampleRate / 1000.0} kHz" else ""
                "$ext$khz • On device"
            }
            else -> "On device"
        }
        _currentFormatDescription.value = desc
    }

    private fun formatFromAudioFormat(format: Format): String {
        val codec = format.sampleMimeType?.substringAfterLast('/')?.uppercase() ?: "AUDIO"
        val sampleRate = if (format.sampleRate > 0) " • ${format.sampleRate / 1000.0} kHz" else ""
        return "$codec$sampleRate • On device"
    }

    private fun syncQueueWithController() {
        val player = controller ?: return
        _currentIndex.value = player.currentMediaItemIndex.coerceAtLeast(0)

        val count = player.mediaItemCount
        val mediaIds = (0 until count).mapNotNull { player.getMediaItemAt(it).mediaId.toLongOrNull() }
        val currentIds = _queue.value.map { it.mediaStoreId }

        // Keep it efficient for 400+ songs: rebuild only when the list of mediaIds actually changed
        if (mediaIds == currentIds) {
            return
        }

        scope.launch(Dispatchers.IO) {
            val songsMap = songDao.getSongsByIds(mediaIds).associateBy { it.mediaStoreId }
            val newQueue = mediaIds.mapNotNull { songsMap[it] }
            withContext(Dispatchers.Main) {
                _queue.value = newQueue
            }
        }
    }

    // 500 ms position ticker
    private fun startPositionTicker() {
        positionTickerJob?.cancel()
        positionTickerJob = scope.launch {
            while (isActive) {
                val pos = controller?.currentPosition ?: 0L
                _positionMs.value = pos
                delay(500)
            }
        }
    }

    private fun stopPositionTicker() {
        positionTickerJob?.cancel()
        positionTickerJob = null
        _positionMs.value = controller?.currentPosition ?: 0L
    }

    // Actions
    fun playSong(song: SongEntity, songList: List<SongEntity>) {
        val player = controller ?: return
        Log.d("PlayerController", "playSong called: title='${song.title}', queue size=${songList.size}")
        _queue.value = songList

        val mediaItems = songList.map { it.toMediaItem() }
        val targetIndex = songList.indexOfFirst { it.mediaStoreId == song.mediaStoreId }.coerceAtLeast(0)

        player.setMediaItems(mediaItems, targetIndex, 0L)
        player.prepare()
        player.play()
    }

    fun playPause() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }
    }

    fun next() {
        val player = controller ?: return
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    fun previous() {
        val player = controller ?: return
        if (player.currentPosition > 3000L) {
            player.seekTo(0L)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        } else {
            player.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        val player = controller ?: return
        player.seekTo(positionMs)
        _positionMs.value = positionMs
    }

    fun toggleShuffle() {
        val player = controller ?: return
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }

    fun toggleRepeat() {
        val player = controller ?: return
        val nextMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        player.repeatMode = nextMode
    }

    fun playNext(song: SongEntity) {
        val player = controller ?: return
        val currentIdx = player.currentMediaItemIndex
        val insertIndex = (currentIdx + 1).coerceAtMost(player.mediaItemCount)
        player.addMediaItem(insertIndex, song.toMediaItem())

        val updatedQueue = _queue.value.toMutableList()
        val queueInsertIdx = (currentIdx + 1).coerceAtMost(updatedQueue.size)
        updatedQueue.add(queueInsertIdx, song)
        _queue.value = updatedQueue
    }

    fun addToQueue(song: SongEntity) {
        val player = controller ?: return
        player.addMediaItem(song.toMediaItem())
        val updatedQueue = _queue.value.toMutableList()
        updatedQueue.add(song)
        _queue.value = updatedQueue
    }

    fun removeFromQueue(index: Int) {
        val player = controller ?: return
        if (index in 0 until player.mediaItemCount) {
            player.removeMediaItem(index)
            val updatedQueue = _queue.value.toMutableList()
            if (index in updatedQueue.indices) {
                updatedQueue.removeAt(index)
                _queue.value = updatedQueue
            }
        }
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val player = controller ?: return
        if (fromIndex in 0 until player.mediaItemCount && toIndex in 0 until player.mediaItemCount) {
            player.moveMediaItem(fromIndex, toIndex)
            val updatedQueue = _queue.value.toMutableList()
            if (fromIndex in updatedQueue.indices && toIndex in updatedQueue.indices) {
                val item = updatedQueue.removeAt(fromIndex)
                updatedQueue.add(toIndex, item)
                _queue.value = updatedQueue
            }
        }
    }

    fun clearQueue() {
        val player = controller ?: return
        player.clearMediaItems()
        _queue.value = emptyList()
        _currentSong.value = null
        _isPlaying.value = false
        savePlaybackState()
    }

    // Persistence to Room (Requirement 2 & 9)
    fun savePlaybackState() {
        if (isRestoringSavedState) return
        val current = _currentSong.value ?: return
        val currentPos = controller?.currentPosition ?: _positionMs.value
        val queueList = _queue.value
        val queueIdsString = queueList.joinToString(",") { it.mediaStoreId.toString() }
        val index = _currentIndex.value
        val shuffle = _shuffleEnabled.value
        val repeat = _repeatMode.value

        scope.launch(Dispatchers.IO) {
            try {
                playbackStateDao.savePlaybackState(
                    PlaybackStateEntity(
                        id = 1,
                        currentSongId = current.mediaStoreId,
                        positionMs = currentPos,
                        queueSongIds = queueIdsString,
                        currentIndex = index,
                        shuffleEnabled = shuffle,
                        repeatMode = repeat
                    )
                )
            } catch (e: Exception) {
                Log.e("PlayerController", "Failed to save playback state to Room", e)
            }
        }
    }

    private suspend fun restorePlaybackState() = withContext(Dispatchers.IO) {
        val saved = try {
            playbackStateDao.getPlaybackState()
        } catch (e: Exception) {
            null
        } ?: return@withContext

        if (saved.queueSongIds.isBlank()) return@withContext

        val idStrings = saved.queueSongIds.split(",")
        val ids = idStrings.mapNotNull { it.trim().toLongOrNull() }
        if (ids.isEmpty()) return@withContext

        // Skip songs that no longer exist in Room/MediaStore without crashing
        val foundSongsMap = songDao.getSongsByIds(ids).associateBy { it.mediaStoreId }
        val restoredQueue = ids.mapNotNull { foundSongsMap[it] }

        Log.d("PlayerController", "restore result: restored ${restoredQueue.size} songs, skipped ${ids.size - restoredQueue.size} missing songs")

        if (restoredQueue.isEmpty()) return@withContext

        withContext(Dispatchers.Main) {
            val player = controller ?: return@withContext
            // Don't overwrite if user already started playing something
            if (player.isPlaying || player.mediaItemCount > 0) return@withContext

            isRestoringSavedState = true
            _queue.value = restoredQueue

            val mediaItems = restoredQueue.map { it.toMediaItem() }
            val targetIdx = saved.currentIndex.coerceIn(0, (restoredQueue.size - 1).coerceAtLeast(0))

            player.setMediaItems(mediaItems, targetIdx, saved.positionMs)
            player.shuffleModeEnabled = saved.shuffleEnabled
            player.repeatMode = saved.repeatMode
            player.prepare() // Prepared without calling play() so app resumes without auto-playing

            _currentSong.value = restoredQueue.getOrNull(targetIdx)
            _positionMs.value = saved.positionMs
            _durationMs.value = restoredQueue.getOrNull(targetIdx)?.durationMs?.coerceAtLeast(1L) ?: 1L
            _currentIndex.value = targetIdx
            _shuffleEnabled.value = saved.shuffleEnabled
            _repeatMode.value = saved.repeatMode
            updateFormatDescription()
            isRestoringSavedState = false
        }
    }

    fun release() {
        savePlaybackState()
        stopPositionTicker()
        controller?.release()
        controller = null
    }
}

// Convert SongEntity to Media3 MediaItem
fun SongEntity.toMediaItem(): MediaItem {
    // Set artworkUri to the song's own content URI so BitmapLoader can use ContentResolver.loadThumbnail(songUri, Size(512, 512))
    val artworkUri = Uri.parse(contentUriString)

    return MediaItem.Builder()
        .setMediaId(mediaStoreId.toString())
        .setUri(contentUriString)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setArtworkUri(artworkUri)
                .build()
        )
        .build()
}
