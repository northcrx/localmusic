package com.example.droidlab.data.dao

import androidx.room.*
import com.example.droidlab.data.entity.SongEntity
import kotlinx.coroutines.flow.Flow

data class AlbumSummary(
    val album: String,
    val artist: String,
    val trackCount: Int,
    val albumId: Long,
    val year: Int
)

data class ArtistSummary(
    val artist: String,
    val albumsCount: Int,
    val songsCount: Int,
    val artistId: Long
)

data class SongIdModified(
    val mediaStoreId: Long,
    val dateModified: Long
)

@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongsRecentlyAdded(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE ASC")
    fun getAllSongsByTitle(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY artist COLLATE NOCASE ASC, title COLLATE NOCASE ASC")
    fun getAllSongsByArtist(): Flow<List<SongEntity>>

    @Query("""
        SELECT album, artist, COUNT(*) as trackCount, albumId, MAX(year) as year
        FROM songs
        GROUP BY album, artist
        ORDER BY album COLLATE NOCASE ASC
    """)
    fun getDistinctAlbums(): Flow<List<AlbumSummary>>

    @Query("""
        SELECT artist, COUNT(DISTINCT album) as albumsCount, COUNT(*) as songsCount, artistId
        FROM songs
        GROUP BY artist
        ORDER BY artist COLLATE NOCASE ASC
    """)
    fun getDistinctArtists(): Flow<List<ArtistSummary>>

    @Query("SELECT mediaStoreId, dateModified FROM songs")
    suspend fun getAllExistingIdsAndModified(): List<SongIdModified>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM songs WHERE mediaStoreId NOT IN (:activeIds)")
    suspend fun deleteSongsNotIn(activeIds: List<Long>)

    @Query("DELETE FROM songs WHERE mediaStoreId = :mediaStoreId")
    suspend fun deleteSong(mediaStoreId: Long)

    @Query("DELETE FROM songs")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM songs")
    fun getSongCount(): Flow<Int>

    @Query("SELECT DISTINCT relativePath FROM songs WHERE relativePath != '' ORDER BY relativePath ASC")
    fun getDistinctRelativePaths(): Flow<List<String>>
}
