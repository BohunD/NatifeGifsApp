package com.bohunapps.natifegifsapp.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.bohunapps.natifegifsapp.data.database.BannedGifEntity
import com.bohunapps.natifegifsapp.data.database.GifDatabase
import com.bohunapps.natifegifsapp.data.database.GifEntity
import com.bohunapps.natifegifsapp.data.mapper.toModel
import com.bohunapps.natifegifsapp.data.models.Images
import com.bohunapps.natifegifsapp.data.models.Original
import com.bohunapps.natifegifsapp.data.retrofit.GifsApiService
import com.bohunapps.natifegifsapp.domain.model.Gif
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import com.bohunapps.natifegifsapp.utils.GifsPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class GifRepositoryImpl @Inject constructor(
    private val apiService: GifsApiService,
    private val apiKey: String,
    private val database: GifDatabase,
    private val context: Context
) : GifRepository {

    override suspend fun fetchTrendingGifs(): Flow<PagingData<GifImage>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GifsPagingSource(apiService, apiKey) }
        )

        return pager.flow.map { pagingData ->
            val bannedIds = database.bannedGifDao().getAllBannedGifIds()
            pagingData.filter { !bannedIds.contains(it.id) }
        }
    }

    override suspend fun searchGifs(query: String): Flow<PagingData<GifImage>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GifsPagingSource(apiService, apiKey, query)
            }
        )

        return pager.flow.map { pagingData ->
            val bannedIds = database.bannedGifDao().getAllBannedGifIds()
            pagingData.filter { !bannedIds.contains(it.id) }
        }
    }

    override suspend fun saveGifToInternalStorage(gif: GifImage) {
        try {
            val url = URL(gif.images.original.url)
            val inputStream: InputStream =
                withContext(Dispatchers.IO) {
                    url.openConnection()
                        .getInputStream()
                }
            val file = File(context.filesDir, "${gif.id}.gif")
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override suspend fun saveGifToDatabase(gif: GifImage) {
        database.gifDao().insertGif(GifEntity(gif.id, gif.images.original.url))
    }

    override suspend fun getGifFromInternalStorage(id: String): File? {
        val file = File(context.filesDir, "$id.gif")
        return if (file.exists()) file else null
    }

    override suspend fun removeGifFromSaved(id: String) {
        database.gifDao().deleteGifById(id)
        deleteGifFromStorage(id)
    }

    override suspend fun getSavedGifs(): Flow<List<GifImage>> {
        return flow {
            val savedGifs = context.filesDir.listFiles()?.mapNotNull { file ->
                if (file.extension == "gif") {
                    val gifId = file.nameWithoutExtension
                    GifImage(gifId, Images(Original(file.toUri().toString())))
                } else {
                    null
                }
            } ?: emptyList()

            emit(savedGifs)
        }
    }

    override suspend fun banGif(id: String) {
        database.bannedGifDao().insertBannedGif(BannedGifEntity(id))
        database.gifDao().deleteGifById(id)
        deleteGifFromStorage(id)
    }

    private fun deleteGifFromStorage(id: String) {
        val fileName = "$id.gif"
        val file = File(context.filesDir, fileName)

        if (file.exists()) {
            file.delete()
        }
    }
}
