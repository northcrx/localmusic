package com.example.droidlab.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    indices = [
        Index(value = ["title"]),
        Index(value = ["artist"]),
        Index(value = ["album"]),
        Index(value = ["dateAdded"])
    ]
)
data class SongEntity(
    @PrimaryKey
    val mediaStoreId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUriString: String,
    val relativePath: String,
    val mimeType: String,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val bitrate: Int = 0,
    val sampleRate: Int = 0,
    val dateAdded: Long = 0L,
    val dateModified: Long = 0L,
    val sizeBytes: Long = 0L,
    val albumId: Long = 0L,
    val artistId: Long = 0L
)
