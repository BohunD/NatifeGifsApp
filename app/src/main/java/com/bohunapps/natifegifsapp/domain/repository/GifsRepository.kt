package com.bohunapps.natifegifsapp.domain.repository

import androidx.paging.PagingData
import com.bohunapps.natifegifsapp.domain.model.Gif
import com.bohunapps.natifegifsapp.domain.model.GifImage
import kotlinx.coroutines.flow.Flow
import java.io.File

interface GifRepository {
    suspend fun fetchTrendingGifs(): Flow<PagingData<GifImage>>
    suspend fun searchGifs(query: String): Flow<PagingData<GifImage>>
    suspend fun saveGifToDatabase(gif: GifImage)
    suspend fun getSavedGifs(): Flow<List<GifImage>>
    suspend fun saveGifToInternalStorage(gif: GifImage)
    suspend fun banGif(id: String)
    suspend fun getGifFromInternalStorage(id: String): File?
    suspend fun removeGifFromSaved(id: String)
}
