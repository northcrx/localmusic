package com.example.droidlab.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.droidlab.data.dao.PlaybackStateDao
import com.example.droidlab.data.dao.SongDao
import com.example.droidlab.data.entity.PlaybackStateEntity
import com.example.droidlab.data.entity.SongEntity

@Database(
    entities = [SongEntity::class, PlaybackStateEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun playbackStateDao(): PlaybackStateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "droidlab_music.db"
                )
                    // TODO: Replace fallbackToDestructiveMigration with explicit Migration classes before production release
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
