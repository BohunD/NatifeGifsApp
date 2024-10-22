package com.bohunapps.natifegifsapp.data.mapper

import com.bohunapps.natifegifsapp.data.database.GifEntity
import com.bohunapps.natifegifsapp.domain.model.Gif

fun GifEntity.toModel(): Gif {
    return Gif(
        id = this.id,
        url = this.url
    )
}

fun Gif.toEntity(): GifEntity {
    return GifEntity(
        id = this.id,
        url = this.url
    )
}
