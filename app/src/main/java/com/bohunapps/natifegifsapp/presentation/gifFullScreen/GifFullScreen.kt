package com.bohunapps.natifegifsapp.presentation.gifFullScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.bohunapps.natifegifsapp.R
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel.GifViewModel
import com.bohunapps.natifegifsapp.presentation.util.ImageLoadingManager
import com.bohunapps.natifegifsapp.ui.mvi.use
import kotlinx.coroutines.flow.Flow

@Composable
fun GifFullScreenRoute(id: String, onBackPressed: () -> Unit) {
    val viewModel: GifViewModel = hiltViewModel()
    val (state) = use(viewModel)
    GifFullScreen(id = id, gifsFlow = state.gifsListFlow) {
        onBackPressed()
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifFullScreen(
    id: String,
    gifsFlow: Flow<PagingData<GifImage>>,
    onBackPressed: () -> Unit
) {
    val gifsList = gifsFlow.collectAsLazyPagingItems()

    if (gifsList.itemCount > 0) {

        val initialPage = gifsList.itemSnapshotList.items.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: 0

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { gifsList.itemCount },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp)
                    .size(30.dp)
                    .clickable { onBackPressed() },
                tint = Color.Black
            )
            val context = LocalContext.current

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                gifsList[page]?.let { gifItem ->
                    val imageRequest = ImageLoadingManager.getRequest(
                        context,
                        gifItem.images.original.url,
                        onError = {},
                        onSuccess = {}
                    )

                    val imageLoader = ImageLoadingManager.getLoader(context)
                    AsyncImage(
                        model = imageRequest,
                        imageLoader = imageLoader,
                        contentDescription = "GIF",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.ic_error),
                    )
                }
            }
        }
    }
}

