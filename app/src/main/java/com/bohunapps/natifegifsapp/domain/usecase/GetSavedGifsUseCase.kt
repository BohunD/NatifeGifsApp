package com.bohunapps.natifegifsapp.domain.usecase

import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import kotlinx.coroutines.flow.Flow

class GetSavedGifsUseCase(private val repository: GifRepository) {
    suspend operator fun invoke(): Flow<List<GifImage>> {
        return repository.getSavedGifs()
    }
}
