package com.example.droidlab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.droidlab.data.entity.SongEntity
import com.example.droidlab.data.repository.MusicRepository
import com.example.droidlab.di.AppContainer
import com.example.droidlab.scanner.ScanProgress
import com.example.droidlab.ui.screens.AlbumItem
import com.example.droidlab.ui.screens.ArtistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MusicViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _sortOption = MutableStateFlow("Recently added")
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    val songs: StateFlow<List<SongEntity>> = _sortOption.flatMapLatest { sort ->
        when (sort) {
            "Title (A–Z)" -> musicRepository.songsByTitle
            "Artist (A–Z)" -> musicRepository.songsByArtist
            else -> musicRepository.songsRecentlyAdded
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val albums: StateFlow<List<AlbumItem>> = musicRepository.albums.map { summaries ->
        summaries.mapIndexed { index, summary ->
            AlbumItem(
                id = index + 1,
                title = summary.album,
                artist = summary.artist,
                trackCount = summary.trackCount,
                year = if (summary.year > 0) summary.year.toString() else "2024",
                albumId = summary.albumId
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val artists: StateFlow<List<ArtistItem>> = musicRepository.artists.map { summaries ->
        summaries.mapIndexed { index, summary ->
            ArtistItem(
                id = index + 1,
                name = summary.artist,
                albumsCount = summary.albumsCount,
                songsCount = summary.songsCount
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val distinctFolders: StateFlow<List<String>> = musicRepository.distinctFolders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val scanState: StateFlow<ScanProgress> = musicRepository.scanState

    fun setSortOption(option: String) {
        _sortOption.value = option
    }

    fun startScan(allowedPaths: Set<String>? = null) {
        viewModelScope.launch {
            musicRepository.scanMusic(allowedPaths)
        }
    }

    companion object {
        fun provideFactory(appContainer: AppContainer): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
                        return MusicViewModel(appContainer.musicRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}
