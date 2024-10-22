package com.bohunapps.natifegifsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.bohunapps.natifegifsapp.presentation.gifFullScreen.gifFullScreen
import com.bohunapps.natifegifsapp.presentation.gifFullScreen.navigateToGifFull
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.GIFS_LIST_ROUTE
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.gifsListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = GIFS_LIST_ROUTE
) {
    NavHost(navController = navController, startDestination = startDestination ){
        gifsListScreen { gif->
            navController.navigateToGifFull(gif.id)
        }

        gifFullScreen{
            navController.popBackStack()
        }
    }
}