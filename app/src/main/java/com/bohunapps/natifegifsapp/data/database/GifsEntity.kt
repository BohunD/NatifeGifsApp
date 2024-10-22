package com.bohunapps.natifegifsapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifs")
data class GifEntity(
    @PrimaryKey val id: String,
    val url: String
)

@Entity(tableName = "banned_gifs")
data class BannedGifEntity(
    @PrimaryKey val id: String
)