package com.example.droidlab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.droidlab.data.entity.PlaybackStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackStateDao {
    @Query("SELECT * FROM playback_state WHERE id = 1 LIMIT 1")
    suspend fun getPlaybackState(): PlaybackStateEntity?

    @Query("SELECT * FROM playback_state WHERE id = 1 LIMIT 1")
    fun getPlaybackStateFlow(): Flow<PlaybackStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackState(state: PlaybackStateEntity)
}
