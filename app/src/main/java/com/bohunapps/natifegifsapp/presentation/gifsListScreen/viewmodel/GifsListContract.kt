package com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel

import androidx.paging.PagingData
import com.bohunapps.natifegifsapp.data.database.GifEntity
import com.bohunapps.natifegifsapp.domain.model.GifImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.annotation.concurrent.Immutable

interface GifsListContract {

    @Immutable
    data class State(
        val gifsListFlow: MutableStateFlow<PagingData<GifImage>> = MutableStateFlow(PagingData.empty()),
        val currentQuery: String = "",
        val showGif: Boolean = false,
        val selectedGif: GifImage?=null,
        val isOffline: Boolean?=null
    )

    sealed interface Event{
        data object GetGifs: Event
        data class SetQuery(val query: String): Event
        data class BanGif(val gif: GifImage): Event
        data class RemoveGifFromSaved(val gif: GifImage): Event
        data class SaveGif(val gif: GifImage): Event
        data class SetFullGifVisibility(val flag: Boolean): Event
        data class SelectGif(val gif: GifImage): Event
    }

    sealed interface Effect{

    }
}