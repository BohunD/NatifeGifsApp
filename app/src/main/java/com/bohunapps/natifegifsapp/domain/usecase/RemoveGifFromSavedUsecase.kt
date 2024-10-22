package com.bohunapps.natifegifsapp.domain.usecase

import com.bohunapps.natifegifsapp.domain.repository.GifRepository

class RemoveGifFromSavedUsecase(private val repository: GifRepository) {
    suspend operator fun invoke(id: String) {
        repository.removeGifFromSaved(id)
    }
}