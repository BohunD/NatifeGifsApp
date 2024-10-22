package com.bohunapps.natifegifsapp.domain.usecase

import com.bohunapps.natifegifsapp.domain.repository.GifRepository

class BanGifUseCase(private val repository: GifRepository) {
    suspend operator fun invoke(id: String) {
        repository.banGif(id)
    }
}
