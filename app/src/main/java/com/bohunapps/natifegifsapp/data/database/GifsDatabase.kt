package com.bohunapps.natifegifsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GifEntity::class, BannedGifEntity::class], version = 1)
abstract class GifDatabase : RoomDatabase() {
    abstract fun gifDao(): GifsDao
    abstract fun bannedGifDao(): BannedGifsDao

    companion object {
        @Volatile
        private var INSTANCE: GifDatabase? = null

        fun getDatabase(context: Context): GifDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GifDatabase::class.java,
                    "gif_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}