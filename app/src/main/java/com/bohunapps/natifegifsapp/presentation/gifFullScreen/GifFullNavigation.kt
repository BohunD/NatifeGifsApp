package com.bohunapps.natifegifsapp.presentation.gifFullScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val GIF_FULL_ROUTE = "gif_full/{id}"
const val ID = "id"


fun NavController.navigateToGifFull(id:String) = navigate("gif_full/$id")

fun NavGraphBuilder.gifFullScreen(onBackPressed: ()->Unit) {
    composable(
        route = GIF_FULL_ROUTE,
        arguments = listOf(navArgument(ID) { type = NavType.StringType })
    ) { backStackEntry->
        val id = backStackEntry.arguments?.getString(ID) ?: ""
        GifFullScreenRoute(id, onBackPressed)
    }
}