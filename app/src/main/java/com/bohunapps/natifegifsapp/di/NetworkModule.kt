package com.bohunapps.natifegifsapp.di

import android.content.Context
import android.util.Log
import com.bohunapps.natifegifsapp.data.retrofit.GifsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.giphy.com/v1/gifs/"
    private const val API_KEY = "YGHnKKBGSydS6nSt6WAoUcICWwmgCfvL"

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url

                val url = originalHttpUrl.newBuilder()
                    .build()

                val requestBuilder = original.newBuilder()
                    .url(url)
                    .addHeader("api_key", apiKey)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGifApiService(retrofit: Retrofit): GifsApiService {
        return retrofit.create(GifsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiKey(): String {
        return API_KEY
    }

}
