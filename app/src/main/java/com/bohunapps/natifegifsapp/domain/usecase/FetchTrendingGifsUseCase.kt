package com.bohunapps.natifegifsapp.domain.usecase

import androidx.paging.PagingData
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import kotlinx.coroutines.flow.Flow

class FetchTrendingGifsUseCase(private val repository: GifRepository) {
    suspend operator fun invoke(): Flow<PagingData<GifImage>> {
        return repository.fetchTrendingGifs()
    }
}
