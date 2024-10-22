package com.bohunapps.natifegifsapp.data.models

import com.bohunapps.natifegifsapp.domain.model.GifImage

data class GifResponse(
    val data: List<GifImage>
)