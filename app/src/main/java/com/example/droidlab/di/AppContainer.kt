package com.example.droidlab.di

import android.content.Context
import com.example.droidlab.data.db.AppDatabase
import com.example.droidlab.data.repository.MusicRepository
import com.example.droidlab.scanner.MediaStoreScanner

interface AppContainer {
    val musicRepository: MusicRepository
    val mediaStoreScanner: MediaStoreScanner
    val appDatabase: AppDatabase
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val mediaStoreScanner: MediaStoreScanner by lazy {
        MediaStoreScanner(context, appDatabase.songDao())
    }

    override val musicRepository: MusicRepository by lazy {
        MusicRepository(appDatabase.songDao(), mediaStoreScanner)
    }
}
