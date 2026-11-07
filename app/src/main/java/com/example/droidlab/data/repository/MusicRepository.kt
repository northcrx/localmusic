package com.example.droidlab.data.repository

import com.example.droidlab.data.dao.AlbumSummary
import com.example.droidlab.data.dao.ArtistSummary
import com.example.droidlab.data.dao.SongDao
import com.example.droidlab.data.entity.SongEntity
import com.example.droidlab.scanner.MediaStoreScanner
import com.example.droidlab.scanner.ScanProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class MusicRepository(
    private val songDao: SongDao,
    private val mediaStoreScanner: MediaStoreScanner
) {
    val songsRecentlyAdded: Flow<List<SongEntity>> = songDao.getAllSongsRecentlyAdded()
    val songsByTitle: Flow<List<SongEntity>> = songDao.getAllSongsByTitle()
    val songsByArtist: Flow<List<SongEntity>> = songDao.getAllSongsByArtist()

    val albums: Flow<List<AlbumSummary>> = songDao.getDistinctAlbums()
    val artists: Flow<List<ArtistSummary>> = songDao.getDistinctArtists()

    val totalSongsCount: Flow<Int> = songDao.getSongCount()
    val distinctFolders: Flow<List<String>> = songDao.getDistinctRelativePaths()

    val scanState: StateFlow<ScanProgress> = mediaStoreScanner.scanState

    suspend fun scanMusic(allowedRelativePaths: Set<String>? = null): Int {
        return mediaStoreScanner.scanMusic(allowedRelativePaths)
    }
}
