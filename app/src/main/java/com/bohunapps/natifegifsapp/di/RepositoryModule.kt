package com.bohunapps.natifegifsapp.di

import android.content.Context
import com.bohunapps.natifegifsapp.data.database.GifDatabase
import com.bohunapps.natifegifsapp.domain.repository.GifRepository
import com.bohunapps.natifegifsapp.data.repository.GifRepositoryImpl
import com.bohunapps.natifegifsapp.data.retrofit.GifsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideGifRepository(
        apiService: GifsApiService,
        apiKey: String,
        database: GifDatabase,
        @ApplicationContext context: Context
    ): GifRepository {
        return GifRepositoryImpl(apiService, apiKey, database, context)
    }

    @Provides
    @Singleton
    fun provideGifDatabase(@ApplicationContext context: Context): GifDatabase {
        return GifDatabase.getDatabase(context)
    }
}
