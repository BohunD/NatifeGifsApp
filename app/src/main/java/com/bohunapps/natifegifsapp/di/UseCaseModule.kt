package com.bohunapps.natifegifsapp.di

import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import com.bohunapps.natifegifsapp.domain.usecase.BanGifUseCase
import com.bohunapps.natifegifsapp.domain.usecase.FetchTrendingGifsUseCase
import com.bohunapps.natifegifsapp.domain.usecase.GetSavedGifsUseCase
import com.bohunapps.natifegifsapp.domain.usecase.RemoveGifFromSavedUsecase
import com.bohunapps.natifegifsapp.domain.usecase.SaveGifUseCase
import com.bohunapps.natifegifsapp.domain.usecase.SearchGifsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideFetchTrendingGifsUseCase(
        repository: GifRepository
    ): FetchTrendingGifsUseCase {
        return FetchTrendingGifsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchGifsUseCase(
        repository: GifRepository
    ): SearchGifsUseCase {
        return SearchGifsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideBanGifUseCase(
        repository: GifRepository
    ): BanGifUseCase {
        return BanGifUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveGifFromSavedUseCase(
        repository: GifRepository
    ): RemoveGifFromSavedUsecase {
        return RemoveGifFromSavedUsecase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSavedGifsUseCase(
        repository: GifRepository
    ): GetSavedGifsUseCase {
        return GetSavedGifsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveGifToDatabaseUseCase(
        repository: GifRepository
    ): SaveGifUseCase {
        return SaveGifUseCase(repository)
    }

}
