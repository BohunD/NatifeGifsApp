package com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel

import android.content.Context
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
import com.bohunapps.natifegifsapp.presentation.util.NetworkMonitor
import com.bohunapps.natifegifsapp.ui.mvi.UnidirectionalViewModel
import com.bohunapps.natifegifsapp.ui.mvi.mvi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
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
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor
) : ViewModel(),
    UnidirectionalViewModel<GifsListContract.State, GifsListContract.Event, GifsListContract.Effect> by mvi(
        GifsListContract.State(),
    ) {

    init {
        networkMonitor.startMonitoring()
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            networkMonitor.isConnected.collectLatest { isConnected ->
                updateUiState { copy(isOffline = !isConnected) }
                getGifs()
            }
        }
    }

    private suspend fun GifViewModel.getGifs(
    ) {
        if (state.value.isOffline == false) {
            if(state.value.currentQuery.isEmpty())
                fetchOnlineGifs()
            else
                searchGifs()
        } else {
            loadOfflineGifs()
        }
    }

    override fun event(event: GifsListContract.Event) = when (event) {
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
        is GifsListContract.Event.SetFullGifVisibility -> {
            updateUiState { copy(showGif = event.flag) }
        }
        is GifsListContract.Event.SelectGif -> {
            updateUiState { copy(selectedGif = event.gif) }
        }
        is GifsListContract.Event.RemoveGifFromSaved -> {
            removeGifFromSaved(event.gif.id)
        }

        GifsListContract.Event.GetGifs -> {
            getGifsFromEvent()
        }
    }

    private fun getGifsFromEvent(){
        viewModelScope.launch {
            getGifs()
        }
    }

    private fun saveGif(gif: GifImage) {
        viewModelScope.launch(Dispatchers.IO) {
            saveGifUseCase.invoke(gif)
        }
    }

    private fun banGif(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            banGifUseCase.invoke(id)
            if(state.value.isOffline == true){
                loadOfflineGifs()
            }else {
                if (state.value.currentQuery.isNotEmpty()) {
                    searchGifs()
                } else {
                    fetchOnlineGifs()
                }
            }
        }
    }

    private fun removeGifFromSaved(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeGifFromSavedUsecase.invoke(id)
        }
    }

    private suspend fun fetchOnlineGifs() {
        try {
            fetchTrendingGifsUseCase.invoke()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                   // state.value.gifsListFlow.emit(it)
                    val tempListFlow = state.value.gifsListFlow
                    tempListFlow.emit(it)
                    updateUiState { copy(gifsListFlow = tempListFlow) }
                }
        } catch (e: Exception) {
            Log.d("EXCEPTION: ", e.message.toString())
        }
    }

    private suspend fun loadOfflineGifs() {
        try {
            getSavedGifsUseCase.invoke().map { savedGifs ->
                PagingData.from(savedGifs)
            }
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    state.value.gifsListFlow.emit(it)
                    val tempListFlow = state.value.gifsListFlow
                    tempListFlow.emit(it)
                    updateUiState { copy(gifsListFlow = tempListFlow) }
                }
        } catch (e: Exception) {
            Log.d("EXCEPTION: ", e.message.toString())
        }
    }

    private fun searchGifs() {
        viewModelScope.launch {
            try {
                val gifs = searchGifsUseCase.invoke(state.value.currentQuery).cachedIn(viewModelScope)
                gifs.collect {
                    val tempListFlow = state.value.gifsListFlow
                    tempListFlow.emit(it)
                    updateUiState { copy(gifsListFlow = tempListFlow) }
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.stopMonitoring()
    }
}
