package com.bohunapps.natifegifsapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BannedGifsDao {

    @Query("SELECT id FROM banned_gifs")
    suspend fun getAllBannedGifIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBannedGif(bannedGif: BannedGifEntity)

    @Query("DELETE FROM banned_gifs WHERE id = :id")
    suspend fun deleteBannedGifById(id: String)
}