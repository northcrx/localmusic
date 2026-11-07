package com.example.droidlab

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.droidlab.di.AppContainer
import com.example.droidlab.di.DefaultAppContainer
import com.example.droidlab.ui.util.AudioThumbnailFetcher

class MusicApplication : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(AudioThumbnailFetcher.Factory(this@MusicApplication))
            }
            .crossfade(true)
            .build()
    }
}
