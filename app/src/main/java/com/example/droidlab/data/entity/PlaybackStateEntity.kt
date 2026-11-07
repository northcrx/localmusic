package com.example.droidlab.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackStateEntity(
    @PrimaryKey val id: Int = 1,
    val currentSongId: Long? = null,
    val positionMs: Long = 0L,
    val queueSongIds: String = "", // Comma-separated mediaStoreIds
    val currentIndex: Int = 0,
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = 0
)
