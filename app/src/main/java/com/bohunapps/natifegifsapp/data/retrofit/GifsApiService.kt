package com.bohunapps.natifegifsapp.data.retrofit

import com.bohunapps.natifegifsapp.data.models.GifResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GifsApiService {
    @GET("trending")
    suspend fun getTrendingGifs(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("api_key") apiKey: String
    ): GifResponse

    @GET("search")
    suspend fun searchGifs(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = "g",
        @Query("lang") lang: String = "en",
        @Query("api_key") apiKey: String
    ): GifResponse
}

