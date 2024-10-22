package com.bohunapps.natifegifsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.bohunapps.natifegifsapp.presentation.gifFullScreen.gifFullScreen
import com.bohunapps.natifegifsapp.presentation.gifFullScreen.navigateToGifFull
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.GIFS_LIST_ROUTE
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.gifsListScreen
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel.GifViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = GIFS_LIST_ROUTE,
    viewModel: GifViewModel = hiltViewModel() // Створюємо один раз
) {
    NavHost(navController = navController, startDestination = startDestination ) {
        gifsListScreen(viewModel) { gif ->
            navController.navigateToGifFull(gif.id)
        }

        gifFullScreen(viewModel) {
            navController.popBackStack()
        }
    }
}
