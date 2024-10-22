package com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.domain.usecase.BanGifUseCase
import com.bohunapps.natifegifsapp.domain.usecase.FetchTrendingGifsUseCase
import com.bohunapps.natifegifsapp.domain.usecase.GetSavedGifsUseCase
import com.bohunapps.natifegifsapp.domain.usecase.RemoveGifFromSavedUsecase
import com.bohunapps.natifegifsapp.domain.usecase.SaveGifUseCase
import com.bohunapps.natifegifsapp.domain.usecase.SearchGifsUseCase
import com.bohunapps.natifegifsapp.ui.mvi.UnidirectionalViewModel
import com.bohunapps.natifegifsapp.ui.mvi.mvi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GifViewModel @Inject constructor(
    private val saveGifUseCase: SaveGifUseCase,
    private val banGifUseCase: BanGifUseCase,
    private val removeGifFromSavedUsecase: RemoveGifFromSavedUsecase,
    private val getSavedGifsUseCase: GetSavedGifsUseCase,
    private val fetchTrendingGifsUseCase: FetchTrendingGifsUseCase,
    private val searchGifsUseCase: SearchGifsUseCase,
    @ApplicationContext private val context: Context

) : ViewModel(),
    UnidirectionalViewModel<GifsListContract.State, GifsListContract.Event, GifsListContract.Effect> by mvi(
        GifsListContract.State(),
    ) {



    init {
        viewModelScope.launch {
            getGifs()
        }
    }

    override fun event(event: GifsListContract.Event) = when (event) {
        is GifsListContract.Event.SearchGifs -> {
            searchGifs()
        }

        is GifsListContract.Event.SetQuery -> {
            updateUiState {
                copy(currentQuery = event.query)
            }
            searchGifs()
        }

        is GifsListContract.Event.BanGif -> {
            banGif(event.gif.id)
        }

        is GifsListContract.Event.SaveGif -> {
            saveGif(event.gif)
        }
        is GifsListContract.Event.SetFullGifVisibility ->{
            updateUiState { copy(showGif = event.flag) }
        }
        is GifsListContract.Event.SelectGif ->{
            updateUiState { copy(selectedGif = event.gif) }
        }

        is GifsListContract.Event.RemoveGifFromSaved -> {
            removeGifFromSaved(event.gif.id)
        }
    }

    private fun saveGif(gif: GifImage) {
        if(isInternetAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                saveGifUseCase.invoke(gif)
            }
        }
    }

    private fun banGif(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            banGifUseCase.invoke(id)
            if (state.value.currentQuery.isNotEmpty()) {
                searchGifs()
            } else {
                getGifs()
            }

        }
    }
    private fun removeGifFromSaved(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeGifFromSavedUsecase.invoke(id)
        }
    }

    private suspend fun getGifs() {
        try {
            if (!isInternetAvailable()) {
                val list = getSavedGifsUseCase.invoke().map { savedGifs ->
                    PagingData.from(savedGifs)
                }
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    state.value.gifsListFlow.value = list.stateIn(viewModelScope).value
                updateUiState { copy(isOffline = true) }

            } else {
                fetchTrendingGifsUseCase.invoke()
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        state.value.gifsListFlow.value = it
                    }
                updateUiState { copy(isOffline = false) }

            }
        } catch (e: Exception) {
            Log.d("EXCEPTION: ", e.message.toString())
        }

    }


    private fun searchGifs() {
        viewModelScope.launch {
            try {
                val gifs = searchGifsUseCase.invoke(state.value.currentQuery)
                gifs.collect {
                    state.value.gifsListFlow.value = it
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
            }
        }
    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}