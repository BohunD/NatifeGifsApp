package com.bohunapps.natifegifsapp.presentation.gifsListScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bohunapps.natifegifsapp.domain.model.GifImage

const val GIFS_LIST_ROUTE = "gifs_list"

fun NavController.navigateToGifsList() = navigate(GIFS_LIST_ROUTE)

fun NavGraphBuilder.gifsListScreen(onGifClicked: (GifImage)->Unit) {
    composable(
        route = GIFS_LIST_ROUTE,
    ) {
        GifsListScreenRoute(onGifClicked)
    }
}