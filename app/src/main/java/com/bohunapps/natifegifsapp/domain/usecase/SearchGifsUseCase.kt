package com.bohunapps.natifegifsapp.domain.usecase

import androidx.paging.PagingData
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import kotlinx.coroutines.flow.Flow

class SearchGifsUseCase(private val repository: GifRepository) {
    suspend operator fun invoke(query: String): Flow<PagingData<GifImage>> {
        return repository.searchGifs(query)
    }
}
