package com.bohunapps.natifegifsapp.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bohunapps.natifegifsapp.data.retrofit.GifsApiService
import com.bohunapps.natifegifsapp.domain.model.GifImage

class GifsPagingSource(
    private val apiService: GifsApiService,
    private val apiKey: String,
    private val query: String? = null
) : PagingSource<Int, GifImage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifImage> {
        val position = params.key ?: 0
        val loadSize = 2
        val offset = position * loadSize
        return try {
            val response = if (query.isNullOrEmpty()) {
                apiService.getTrendingGifs(
                    limit = loadSize,
                    offset = offset,
                    apiKey = apiKey
                )
            } else {
                apiService.searchGifs(
                    query = query,
                    limit = loadSize,
                    offset = offset,
                    apiKey = apiKey
                )
            }

            LoadResult.Page(
                data = response.data,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (response.data.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
