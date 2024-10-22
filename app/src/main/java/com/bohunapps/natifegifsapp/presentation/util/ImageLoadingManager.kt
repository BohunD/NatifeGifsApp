package com.bohunapps.natifegifsapp.presentation.util

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bohunapps.natifegifsapp.R

object ImageLoadingManager {
    @Volatile
    private var loader: ImageLoader? = null
    fun getRequest(
        context: Context,
        data: Any?,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ): ImageRequest = ImageRequest.Builder(context)
        .data(data)
        .diskCacheKey(data.toString())
        .memoryCacheKey(data.toString())
        .crossfade(true)
        .placeholder(R.drawable.ic_loading)
        .listener(
            onError = { _, _ -> onError() },
            onSuccess = { _, _ -> onSuccess() } // Trigger the success callback
        )
        .error(R.drawable.ic_error)
        .build()


    fun getLoader(context: Context,): ImageLoader =
        loader ?: synchronized(this) {
            ImageLoader.Builder(context)
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("image_cache"))
                        .maxSizePercent(0.02)
                        .build()
                }
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(0.3)
                        .build()
                }
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .components {
                    if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                    else add(GifDecoder.Factory())
                }
                .respectCacheHeaders(false)
                .build()
                .also {
                    loader = it
                }
        }


}