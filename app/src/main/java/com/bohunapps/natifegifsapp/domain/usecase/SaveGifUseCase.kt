package com.bohunapps.natifegifsapp.domain.usecase

import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.repository.GifRepository

class SaveGifUseCase(private val repository: GifRepository) {
    suspend operator fun invoke(gif: GifImage) {
        repository.saveGifToInternalStorage(gif)
        repository.saveGifToDatabase(gif)
    }
}
