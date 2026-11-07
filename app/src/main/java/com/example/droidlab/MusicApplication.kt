package com.example.droidlab

import android.app.Application
import com.example.droidlab.di.AppContainer
import com.example.droidlab.di.DefaultAppContainer

class MusicApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
