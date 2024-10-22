package com.bohunapps.natifegifsapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GifsDao {

    @Query("SELECT * FROM gifs")
    fun getAllGifs(): Flow<List<GifEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGifs(gifs: List<GifEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGif(gifs: GifEntity)

    @Query("DELETE FROM gifs")
    suspend fun deleteAllGifs()
    @Query("DELETE FROM gifs WHERE id = :gifId")
    suspend fun deleteGifById(gifId: String)
}

