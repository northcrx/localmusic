package com.example.droidlab.playback

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.BitmapLoader
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.droidlab.MainActivity
import com.example.droidlab.R
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executors

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate() {
        super.onCreate()

        // 1. Audio Attributes with AudioFocus handling & Noisy handling
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        // 2. Custom BitmapLoader for Notification & Lock Screen (loadThumbnail API 29+)
        val customBitmapLoader = object : BitmapLoader {
            override fun supportsMimeType(mimeType: String): Boolean = true

            override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                return Futures.immediateFuture(bitmap ?: createPlaceholderBitmap())
            }

            override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
                val future = com.google.common.util.concurrent.SettableFuture.create<Bitmap>()
                executor.execute {
                    try {
                        val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            try {
                                contentResolver.loadThumbnail(uri, Size(512, 512), null)
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            try {
                                contentResolver.openInputStream(uri)?.use { stream ->
                                    BitmapFactory.decodeStream(stream)
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }
                        future.set(bitmap ?: createPlaceholderBitmap())
                    } catch (e: Exception) {
                        future.set(createPlaceholderBitmap())
                    }
                }
                return future
            }

            override fun loadBitmapFromMetadata(metadata: MediaMetadata): ListenableFuture<Bitmap>? {
                val artworkUri = metadata.artworkUri
                return if (artworkUri != null) {
                    loadBitmap(artworkUri)
                } else if (metadata.artworkData != null) {
                    decodeBitmap(metadata.artworkData!!)
                } else {
                    Futures.immediateFuture(createPlaceholderBitmap())
                }
            }
        }

        // 3. PendingIntent to reopen MainActivity from notification
        val sessionActivityIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 4. MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityIntent)
            .setBitmapLoader(customBitmapLoader)
            .setCallback(object : MediaSession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    return Futures.immediateFuture(mediaItems)
                }
            })
            .build()

        // 5. Notification Provider
        val notificationProvider = DefaultMediaNotificationProvider(this)
        notificationProvider.setSmallIcon(R.drawable.ic_msr_play_circle)
        setMediaNotificationProvider(notificationProvider)

        // 6. Error handling: if a song fails to play (deleted/corrupt), skip to next
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlaybackService", "Playback error occurred: ${error.message}", error)
                if (exoPlayer.hasNextMediaItem()) {
                    exoPlayer.seekToNextMediaItem()
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            }
        })
    }

    private fun createPlaceholderBitmap(): Bitmap {
        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.DKGRAY
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        return bitmap
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!exoPlayer.playWhenReady || exoPlayer.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        executor.shutdown()
        super.onDestroy()
    }
}
